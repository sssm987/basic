package com.example.basic.api.queue.controller;

import com.example.basic.api.queue.dto.response.QueueWaitCheckResponseDto;
import com.example.basic.api.queue.dto.response.QueueWaitRegistrationResponseDto;
import com.example.basic.application.queue.service.QueueService;
import com.example.basic.global.common.ResponseWrapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @GetMapping("/wait-check/{memberId}")
    public ResponseEntity<ResponseWrapper<QueueWaitCheckResponseDto>> waitCheck(@PathVariable @Parameter(name = "memberId", description = "회원 번호", example = "1") long memberId){

        return ResponseEntity.ok(ResponseWrapper.of(
                QueueWaitCheckResponseDto.of(queueService.waitCheck(memberId))));
    }
    @PostMapping("/wait-registration")
    public ResponseEntity<ResponseWrapper<QueueWaitRegistrationResponseDto>> waitRegistration(@RequestParam @Parameter(name = "memberId", description = "회원 번호", example = "1") long memberId){

        return ResponseEntity.ok(ResponseWrapper.of(
                QueueWaitRegistrationResponseDto.of(queueService.waitRegistration(memberId))));
    }
}
