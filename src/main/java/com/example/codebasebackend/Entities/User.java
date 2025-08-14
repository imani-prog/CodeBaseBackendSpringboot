package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_username", columnList = "username"),
                @Index(name = "idx_user_email", columnList = "email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_username", columnNames = {"username"}),
                @UniqueConstraint(name = "uk_user_email", columnNames = {"email"})
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String username;

    @Column(length = 150)
    private String email;

    @Column(length = 120)
    private String fullName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}
