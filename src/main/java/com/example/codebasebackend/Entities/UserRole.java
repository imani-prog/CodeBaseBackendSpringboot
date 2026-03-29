package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter

@Entity
@Table
public enum UserRole {
    PATIENT,
    CHW,
    ADMIN,
    ;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

}