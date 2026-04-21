# basic

동시성 문제를 확인하기 위해 주문할 때 재고가 꼬이는 상황을 재현하려고 만든 프로젝트입니다.

락 없이 구현해두고, 같은 상품에 요청이 동시에 들어오면 어떤 문제가 생기는지 보는 용도로 만들었습니다.

## 사용 기술

- Java 21
- Spring Boot
- Spring Data JPA
- H2
- PostgreSQL
- Swagger

## 실행

로컬에서는 H2로 바로 실행됩니다.

```bash
./gradlew bootRun
```

## 프로필

- local: H2
- prod: PostgreSQL

## Swagger

- http://localhost:8080/swagger-ui.html

## API 예시

### 주문 생성

`POST /api/v1/order`

주문생성 요청이며 재고가 있으면 생성이 되고 재고가 1개씩 차감됩니다.

```json
{
  "productId": 1,
  "memberId": 100
}
```

재고가 없으면 아래와 같이 내려옵니다.

```json
{
  "status": 409,
  "code": "PRODUCT_INVENTORY_SHORT",
  "message": "상품의 수량이 부족합니다."
}
```

없는 상품 번호를 보내면 이렇게 내려옵니다.

```json
{
  "status": 404,
  "code": "PRODUCT_NOT_FOUND",
  "message": "상품을 찾을 수 없습니다."
}
```

### 상품별 주문 내역 조회

`GET /api/v1/product`

응답 모든 상품이 조회되며 파라미터는 순서대로 상품번호, 현재재고, 주문횟수, 초기 재고 입니다.

```json
[
  {
    "productId": 1, 
    "stock": 0,
    "orderCount": 3,
    "initiativeStock": 3
  }
]
```

상품별로 초기 재고, 현재 재고, 주문 수를 한 번에 보려고 만든 응답입니다.

## 재현 흐름 예시

1. 서버를 실행합니다.
2. 같은 `productId`로 주문 요청을 여러 번 보냅니다.
3. `GET /api/v1/product`로 결과를 확인합니다.
4. 락이 없을 때 재고가 어떻게 꼬일 수 있는지 봅니다.

## 현재 구현 범위

- 상품 더미 데이터 자동 적재
- 주문 생성 API
- 공통 에러 응답
- 재고 차감 로직
- 락 미적용 상태에서의 재고 문제 확인용 구조

## 프로젝트 목적

동시성 이슈가 어떤식으로 일어나고 부하테스트시 어떤식으로 문제가 일어나는지 측정하려고 만든 프로젝트입니다.
