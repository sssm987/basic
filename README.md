# Concurrency Practice Project

주문 요청이 동시에 들어올 때 상품 재고가 어떻게 깨질 수 있는지 확인하고, 여러 동시성 제어 전략을 브랜치별로 비교하기 위한 토이 프로젝트입니다.

같은 상품에 여러 주문 요청을 동시에 보내고, 주문 수와 남은 재고가 기대한 값으로 맞는지 확인하는 흐름을 기준으로 작성되어 있습니다. `main` 브랜치는 별도 락이나 동시성 제어를 적용하지 않은 기본 샘플이고, 각 전략은 별도 브랜치에서 확인할 수 있습니다.

## 기술 스택

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Querydsl
- H2
- PostgreSQL
- Redis (`waitSystem` 브랜치)
- Swagger / OpenAPI
- Actuator, Prometheus, Grafana

## 브랜치별 예제

| 브랜치 | 전략 | 핵심 아이디어 |
| --- | --- | --- |
| `main` | 락 미적용 기본 샘플 | 같은 상품에 동시 주문이 들어올 때 재고 정합성이 깨질 수 있는 기준 코드입니다. |
| `atomicUpdate` | 원자적 update | `stock > 0` 조건을 포함한 update 쿼리로 재고 차감을 DB 단일 연산으로 처리합니다. |
| `optimisticLock` | 낙관적 락 | `@Version`으로 충돌을 감지하고, 충돌 시 최대 5회 재시도합니다. |
| `pessimisticLock` | 비관적 락 | `PESSIMISTIC_WRITE`로 재고 row를 잠근 뒤 차감합니다. |
| `synchronized` | JVM synchronized | 서비스 메서드에 `synchronized`를 걸어 단일 JVM 안에서만 동시 진입을 막습니다. |
| `waitSystem` | Redis 대기열 | Redis Sorted Set으로 대기/입장 상태를 관리해 주문 진입량을 제어합니다. |

브랜치 전환 예시:

```bash
git switch atomicUpdate
```

`synchronized` 브랜치가 로컬에 없다면 원격 브랜치에서 생성합니다.

```bash
git switch -c synchronized origin/synchronized
```

## 실행

로컬 기본 프로필은 `local`이며 H2 인메모리 DB를 사용합니다.

```bash
./gradlew bootRun
```

Windows PowerShell에서는 다음 명령을 사용할 수 있습니다.

```powershell
.\gradlew.bat bootRun
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

H2 Console:

```text
http://localhost:8080/h2-console
```

## Docker Compose

PostgreSQL, 애플리케이션, Prometheus, Grafana를 함께 실행할 수 있습니다.

```bash
docker compose up --build
```

주요 포트:

| 서비스 | URL |
| --- | --- |
| App | `http://localhost:8080` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3000` |
| PostgreSQL | `localhost:5432` |

현재 `compose.yml`에는 Redis 컨테이너가 포함되어 있지 않습니다. `waitSystem` 브랜치의 대기열 API를 사용하려면 `spring.redis.host`, `spring.redis.port`가 가리키는 Redis가 별도로 실행 중이어야 합니다.

## API

### 주문 생성

```http
POST /api/v1/order
Content-Type: application/json
```

요청:

```json
{
  "productId": 1,
  "memberId": 100
}
```

동작:

- 상품이 없으면 `PRODUCT_NOT_FOUND`를 반환합니다.
- 재고가 없으면 `PRODUCT_INVENTORY_SHORT`를 반환합니다.
- 재고 차감에 성공하면 주문을 저장합니다.

에러 응답 예시:

```json
{
  "status": 409,
  "code": "PRODUCT_INVENTORY_SHORT",
  "message": "상품 재고가 부족합니다."
}
```

### 상품별 주문/재고 조회

```http
GET /api/v1/product
```

응답 예시:

```json
{
  "success": true,
  "data": [
    {
      "productId": 1,
      "stock": 0,
      "orderCount": 3,
      "initiativeStock": 3
    }
  ]
}
```

상품별 초기 재고, 현재 재고, 주문 수를 한 번에 확인하기 위한 조회 API입니다.

### 대기열 등록 (`waitSystem` 브랜치)

```http
POST /api/v1/queue/wait-registration?memberId=1
```

응답 예시:

```json
{
  "success": true,
  "data": {
    "waitingNumber": 0,
    "queueStatus": "ACTIVE"
  }
}
```

대기열이 비어 있고 활성 사용자 수가 제한보다 작으면 바로 `ACTIVE`가 됩니다. 그렇지 않으면 Redis Sorted Set에 대기자로 등록되고 `WAITING` 상태와 대기 순번을 반환합니다.

### 대기열 상태 확인 (`waitSystem` 브랜치)

```http
GET /api/v1/queue/wait-check/1
```

응답 예시:

```json
{
  "success": true,
  "data": {
    "waitingNumber": 3,
    "queueStatus": "WAITING"
  }
}
```

## 동시성 테스트 흐름

1. 서버를 실행합니다.
2. 같은 `productId`로 주문 생성 요청을 동시에 여러 번 보냅니다.
3. `GET /api/v1/product`로 현재 재고와 주문 수를 확인합니다.
4. 브랜치를 바꿔 같은 부하를 반복하고 결과를 비교합니다.

예상 관찰 포인트:

- 동시 요청 수가 초기 재고보다 클 때 초과 주문이 생성되는지
- 최종 재고가 음수가 되거나 주문 수와 재고 차감 수가 어긋나는지
- 충돌이 발생했을 때 실패로 처리하는지, 재시도하는지, 대기시키는지
- 단일 JVM에서만 안전한 방식인지, DB/Redis를 기준으로 분산 환경에서도 안전한지

## 전략별 특징

### main

동시성 제어를 적용하지 않은 기본 샘플입니다. 동시에 여러 요청이 들어왔을 때 재고 차감과 주문 저장 결과가 어긋날 수 있는 문제 상황을 확인하기 위한 기준 코드입니다.

### synchronized

가장 단순한 방식입니다. 하나의 애플리케이션 인스턴스 안에서는 동시에 한 요청만 재고 차감 로직에 들어오게 할 수 있습니다.

단점은 서버가 여러 대일 때 인스턴스 간 동기화가 되지 않는다는 점입니다. 분산 환경의 재고 처리 방식으로는 적합하지 않고, 동시성 문제를 처음 관찰하기 위한 비교군에 가깝습니다.

### pessimisticLock

DB row에 쓰기 락을 걸고 재고를 차감합니다. 충돌 가능성이 높고 반드시 순차 처리가 필요한 작업에서 이해하기 쉬운 방식입니다.

대신 락 대기 시간이 길어질 수 있고, 트래픽이 몰리면 처리량이 떨어질 수 있습니다.

### optimisticLock

데이터를 먼저 읽고 수정한 뒤 커밋 시점에 version 충돌을 감지합니다. 충돌이 적은 환경에서는 락 대기 비용이 낮습니다.

이 프로젝트의 예제는 충돌이 발생하면 `ObjectOptimisticLockingFailureException`을 잡아 최대 5회 재시도합니다. 재시도 횟수를 초과하면 주문 충돌 에러로 처리합니다.

### atomicUpdate

DB에 다음과 같은 조건부 update를 실행해 재고 차감을 단일 연산으로 처리합니다.

```sql
update inventory
set stock = stock - 1
where product_id = ?
  and stock > 0
```

업데이트된 row 수가 0이면 재고 부족으로 판단합니다. 재고 차감처럼 연산이 단순한 경우 구현과 성능의 균형이 좋은 방식입니다.

### waitSystem

Redis를 사용해 사용자를 `ACTIVE` 또는 `WAITING` 상태로 나눕니다.

- `queue:active`: 현재 입장한 사용자
- `queue:wait`: 대기 중인 사용자
- `queue:token:active:member:{memberId}`: 활성 사용자 토큰
- `queue:token:wait:member:{memberId}`: 대기 사용자 토큰

Lua script를 사용해 즉시 입장 가능 여부 확인과 등록을 원자적으로 처리합니다. 현재 예제의 활성 사용자 제한은 코드상 `100`명입니다.

## 모니터링

Actuator Prometheus endpoint:

```text
http://localhost:8080/actuator/prometheus
```

Prometheus와 Grafana는 `docker compose up --build`로 함께 띄울 수 있습니다.

## 프로젝트 목적

이 프로젝트는 정답 하나를 고르기 위한 코드가 아니라, 같은 주문/재고 문제를 여러 방식으로 풀어보며 다음 차이를 비교하기 위한 학습용 코드입니다.

- 애플리케이션 레벨 동기화와 DB 레벨 동기화의 차이
- 낙관적 락과 비관적 락의 충돌 처리 방식 차이
- 조건부 update 방식의 단순함과 한계
- Redis 대기열을 이용한 요청 진입 제어 방식
