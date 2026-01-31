package com.example.codebasebackend.Entities;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@jakarta.persistence.Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "ambulance_equipment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_equipment_name", columnNames = {"name"})
    },
    indexes = {
        @Index(name = "idx_equipment_category", columnList = "category"),
        @Index(name = "idx_equipment_required", columnList = "is_required")
    }
)
public class AmbulanceEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EquipmentCategory category;
    @Column(columnDefinition = "text")
    private String description;
    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = false;
    @ManyToMany(mappedBy = "equipment")
    @Builder.Default
    private Set<Ambulances> ambulances = new HashSet<>();
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
    public enum EquipmentCategory {
        LIFE_SUPPORT,
        DIAGNOSTIC,
        PATIENT_CARE,
        COMMUNICATION,
        SAFETY,
        MONITORING,
        MEDICATION,
        OTHER
    }
}
