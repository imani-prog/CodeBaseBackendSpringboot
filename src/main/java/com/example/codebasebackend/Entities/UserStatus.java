package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter

@Entity
@Table
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

}