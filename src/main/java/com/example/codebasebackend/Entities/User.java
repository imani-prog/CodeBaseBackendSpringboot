package com.example.codebasebackend.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @Column(length = 30)
    private String phone;

    @JsonIgnore  // never expose password hash in any API response
    @Column(length = 120)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UserStatus status;

    private OffsetDateTime lastLoginAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (role == null) role = UserRole.PATIENT;
        if (status == null) status = UserStatus.ACTIVE;
    }
}