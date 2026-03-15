package com.example.codebasebackend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Entity
@Table(name = "health_records",
		indexes = {
				@Index(name = "idx_hr_patient", columnList = "patient_id"),
				@Index(name = "idx_hr_type", columnList = "recordType"),
				@Index(name = "idx_hr_status", columnList = "status"),
				@Index(name = "idx_hr_visit_date", columnList = "visitDate")
		},
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_hr_code", columnNames = {"recordCode"})
		}
)
public class HealthRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 64, unique = true)
	private String recordCode;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctor_id")
	private Doctor doctor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hospital_id")
	private Hospital hospital;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private RecordType recordType;

	@Enumerated(EnumType.STRING)
	@Column(length = 24)
	private RecordStatus status;

	private LocalDate visitDate;

	private LocalDate dueDate;

	@Column(length = 160)
	private String providerName;

	@Column(length = 120)
	private String providerSpecialty;

	@Column(length = 220)
	private String summary;

	@Column(columnDefinition = "text")
	private String notes;

	@Column(length = 220)
	private String diagnosis;

	// Vaccination-specific
	@Column(length = 140)
	private String vaccineName;

	// Prescription-specific
	@Column(length = 160)
	private String medicationName;

	@Column(length = 80)
	private String dosage;

	@Column(length = 120)
	private String frequency;

	@Column(length = 80)
	private String durationText;

	@Column
	private Integer refillsRemaining;

	@Column
	private Integer totalRefills;

	@Builder.Default
	@ElementCollection
	@CollectionTable(name = "health_record_attachments", joinColumns = @JoinColumn(name = "health_record_id"))
	private List<HealthRecordAttachment> attachments = new ArrayList<>();

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private OffsetDateTime updatedAt;

	@PrePersist
	void defaults() {
		if (status == null) status = RecordStatus.COMPLETED;
		if (recordCode == null || recordCode.isBlank()) {
			recordCode = "HR-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
		}
	}

	public enum RecordType { CONSULTATION, VACCINATION, PRESCRIPTION, LAB_RESULT, IMAGING, INSURANCE, DOCUMENT, OTHER }
	public enum RecordStatus { ACTIVE, COMPLETED, UPCOMING, ARCHIVED }
}
