package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.RemittanceAdvice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RemittanceAdviceRepository extends JpaRepository<RemittanceAdvice, Long> {
    List<RemittanceAdvice> findByClaimId(Long claimId);
}

