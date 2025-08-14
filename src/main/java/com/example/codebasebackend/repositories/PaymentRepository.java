package com.example.codebasebackend.repositories;

import com.example.codebasebackend.Entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBillingIdOrderByCreatedAtAsc(Long billingId);
}

