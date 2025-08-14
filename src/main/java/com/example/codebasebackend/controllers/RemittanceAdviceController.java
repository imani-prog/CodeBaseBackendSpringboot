package com.example.codebasebackend.controllers;

import com.example.codebasebackend.dto.RemittanceAdviceRequest;
import com.example.codebasebackend.dto.RemittanceAdviceResponse;
import com.example.codebasebackend.services.RemittanceAdviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/remittances")
@RequiredArgsConstructor
public class RemittanceAdviceController {

    private final RemittanceAdviceService remittanceService;

    @PostMapping
    public ResponseEntity<RemittanceAdviceResponse> create(@Valid @RequestBody RemittanceAdviceRequest request) {
        return ResponseEntity.ok(remittanceService.create(request));
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<List<RemittanceAdviceResponse>> listByClaim(@PathVariable Long claimId) {
        return ResponseEntity.ok(remittanceService.listByClaim(claimId));
    }
}

