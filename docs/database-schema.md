# Medilink Backend Database Schema (Main Entities)

## Scope and assumptions

- Source analyzed: `src/main/java/com/example/codebasebackend/Entities/*.java`.
- DB target from config: PostgreSQL (`spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect`).
- Schema generation mode: `spring.jpa.hibernate.ddl-auto=update`.
- Column names shown below follow entity field names; when `@Column(name = ...)` is defined, that explicit name is used.
- Spring Boot/Hibernate physical naming usually maps camelCase to snake_case in PostgreSQL for implicit column names.

## Domain overview (main tables)

- Identity and access: `users`
- Clinical core: `patients`, `doctors`, `hospitals`, `community_health_workers`, `appointments`, `health_records`
- Telehealth and field ops: `telemedicine_sessions`, `home_visits`
- Pharmacy and treatment: `prescriptions`, `prescription_refill_requests`, `pharmacies`
- Billing and insurance: `billings`, `payments`, `insurance_providers`, `insurance_plans`, `patient_insurance_policies`, `insurance_claims`
- Training: `training_modules`, `training_enrollments`
- Platform/audit: `audit_logs`, `integration_partners`

## Table-by-table schema

### `users`

- **PK**: `id`
- **Fields**: `username`, `email`, `full_name`, `phone`, `password_hash`, `role`, `status`, `last_login_at`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `username`, `email`
  - Enums stored as strings: `role`, `status`

### `hospitals`

- **PK**: `id`
- **Fields**: `code`, `name`, `type`, `registration_number`, `tax_id`, `email`, `main_phone`, `alt_phone`, `fax`, `website`, `address_line1`, `address_line2`, `city`, `state`, `postal_code`, `country`, `latitude`, `longitude`, `admin_contact_name`, `admin_contact_email`, `admin_contact_phone`, `number_of_beds`, `number_of_icu_beds`, `number_of_ambulances`, `services_offered`, `departments`, `facilities`, `operating_hours`, `accepted_insurance`, `status`, `notes`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `registration_number`, `code`
  - Enums stored as strings: `type`, `status`

### `patients`

- **PK**: `id`
- **FKs**: `user_id -> users.id` (from `@JoinColumn(name = "userId")`), `hospital_id -> hospitals.id`
- **Fields**: `first_name`, `middle_name`, `last_name`, `gender`, `date_of_birth`, `email`, `phone`, `secondary_phone`, `address_line1`, `address_line2`, `city`, `state`, `postal_code`, `country`, `latitude`, `longitude`, `national_id`, `insurance_member_id`, `insurance_provider_name`, `emergency_contact_name`, `emergency_contact_relation`, `emergency_contact_phone`, `allergies`, `medications`, `chronic_conditions`, `blood_type`, `preferred_language`, `status`, `marital_status`, `consent_to_share_data`, `sms_opt_in`, `email_opt_in`, `notes`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `email`, `national_id`, `user_id`
  - Enums stored as strings: `gender`, `blood_type`, `status`, `marital_status`

### `doctors`

- **PK**: `id`
- **FKs**: `specialty_id -> specialties.id`, `hospital_id -> hospitals.id`
- **Fields**: `doctor_id`, `first_name`, `middle_name`, `last_name`, `email`, `phone`, `alternative_phone`, `license_number`, `experience`, `rating`, `total_sessions`, `completed_sessions`, `status`, `last_status_update`, `active`, `photo_url`, `bio`, `location`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `doctor_id`, `email`, `license_number`
  - Enums stored as strings: `status`

### `community_health_workers`

- **PK**: `id`
- **FKs**: `user_id -> users.id` (from `@JoinColumn(name = "userId")`), `hospital_id -> hospitals.id`
- **Fields**: `code`, `first_name`, `middle_name`, `last_name`, `email`, `phone`, `address_line1`, `address_line2`, `city`, `state`, `postal_code`, `country`, `latitude`, `longitude`, `region`, `assigned_patients`, `start_date`, `last_status_update`, `monthly_visits`, `success_rate`, `response_time`, `rating`, `status`, `specialization`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `code`, `user_id`
  - Enum stored as string: `status`

### `appointments`

- **PK**: `id`
- **FKs**: `patient_id -> patients.id`, `hospital_id -> hospitals.id`, `chw_id -> community_health_workers.id`, `doctor_id -> doctors.id`
- **Fields**: `appointment_code`, `scheduled_start`, `scheduled_end`, `check_in_time`, `check_out_time`, `status`, `type`, `provider_role`, `provider_name`, `room`, `location`, `reason`, `notes`, `reminder_sent`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `appointment_code`
  - Enums stored as strings: `status`, `type`, `provider_role`

### `health_records`

- **PK**: `id`
- **FKs**: `patient_id -> patients.id`, `doctor_id -> doctors.id`, `hospital_id -> hospitals.id`
- **Fields**: `record_code`, `record_type`, `status`, `visit_date`, `due_date`, `provider_name`, `provider_specialty`, `summary`, `notes`, `diagnosis`, `vaccine_name`, `medication_name`, `dosage`, `frequency`, `duration_text`, `refills_remaining`, `total_refills`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `record_code`
  - Enums stored as strings: `record_type`, `status`

### `telemedicine_sessions`

- **PK**: `id`
- **FKs**: `patient_id -> patients.id`, `doctor_id -> doctors.id`, `appointment_id -> appointments.id` (unique), `hospital_id -> hospitals.id`, `cancelled_by_user_id -> users.id`, `created_by_user_id -> users.id`
- **Fields**: `session_id`, `session_type`, `platform`, `status`, `priority`, `start_time`, `actual_start_time`, `end_time`, `duration`, `planned_duration`, `chief_complaint`, `diagnosis`, `prescription`, `doctor_notes`, `follow_up_required`, `follow_up_date`, `cost`, `actual_cost`, `payment_status`, `payment_reference`, `rating`, `feedback`, `meeting_link`, `meeting_id`, `recording_url`, `recording_enabled`, `cancellation_reason`, `cancelled_at`, `reminder_sent`, `reminder_sent_at`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `session_id`, `appointment_id`
  - Enums stored as strings: `session_type`, `platform`, `status`, `priority`

### `home_visits`

- **PK**: `id`
- **FKs**: `patient_id -> patients.id`, `chw_id -> community_health_workers.id`
- **Fields**: `visit_type`, `status`, `priority`, `scheduled_at`, `completed_at`, `canceled_at`, `location`, `latitude`, `longitude`, `reason`, `notes`, `outcome`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Enums stored as strings: `status`, `priority`

### `prescriptions`

- **PK**: `id`
- **FKs**: `patient_id -> patients.id`, `doctor_id -> doctors.id`, `hospital_id -> hospitals.id`, `pharmacy_id -> pharmacies.id`
- **Fields**: `prescription_code`, `medication_name`, `generic_name`, `dosage`, `frequency`, `instructions`, `purpose`, `warnings`, `prescribed_date`, `start_date`, `end_date`, `total_refills`, `refills_remaining`, `progress_percent`, `next_dose_at`, `reminder_enabled`, `status`, `provider_specialty`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `prescription_code`
  - Enum stored as string: `status`

### `prescription_refill_requests`

- **PK**: `id`
- **FKs**: `prescription_id -> prescriptions.id`, `pharmacy_id -> pharmacies.id`
- **Fields**: `status`, `delivery_method`, `additional_instructions`, `requested_at`, `decided_at`, `updated_at`
- **Constraints/notes**:
  - Enums stored as strings: `status`, `delivery_method`

### `pharmacies`

- **PK**: `id`
- **Fields**: `name`, `address`, `city`, `postal_code`, `phone`, `hours`, `distance_text`, `rating`, `delivery_fee`, `estimated_delivery`, `nhif_accepted`, `offers_delivery`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique composite: (`name`, `address`)

### `billings`

- **PK**: `id`
- **FKs**: `patient_id -> patients.id`, `hospital_id -> hospitals.id`, `integration_partner_id -> integration_partners.id`
- **Fields**: `invoice_number`, `issue_date`, `service_date`, `due_date`, `subtotal`, `discount`, `tax`, `total`, `amount_paid`, `balance`, `currency`, `status`, `notes`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `invoice_number`
  - Enum stored as string: `status`

### `payments`

- **PK**: `id`
- **FKs**: `billing_id -> billings.id`
- **Fields**: `method`, `status`, `amount`, `currency`, `external_reference`, `notes`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Enums stored as strings: `method`, `status`

### `insurance_providers`

- **PK**: `id`
- **FKs**: `integration_partner_id -> integration_partners.id`
- **Fields**: `name`, `payer_id`, `registration_number`, `email`, `phone`, `fax`, `website`, `provider_portal_url`, `claims_submission_url`, `claims_submission_email`, `support_phone`, `support_email`, `address_line1`, `address_line2`, `city`, `state`, `postal_code`, `country`, `status`, `notes`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `name`, `payer_id`
  - Enum stored as string: `status`

### `insurance_plans`

- **PK**: `id`
- **FKs**: `provider_id -> insurance_providers.id`
- **Fields**: `plan_name`, `plan_code`, `plan_type`, `network_type`, `coverage_details`, `deductible_individual`, `deductible_family`, `oop_max_individual`, `oop_max_family`, `copay_primary_care`, `copay_specialist`, `copay_emergency`, `coinsurance_percent`, `requires_referral`, `preauth_required`, `effective_from`, `effective_to`, `status`, `notes`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique composite: (`provider_id`, `plan_code`)
  - Enums stored as strings: `plan_type`, `network_type`, `status`

### `patient_insurance_policies`

- **PK**: `id`
- **FKs**: `patient_id -> patients.id`, `provider_id -> insurance_providers.id`, `plan_id -> insurance_plans.id`
- **Fields**: `member_id`, `group_number`, `coverage_level`, `policyholder_name`, `policyholder_relation`, `policyholder_dob`, `effective_from`, `effective_to`, `status`, `card_front_url`, `card_back_url`, `notes`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique composite: (`provider_id`, `member_id`)
  - Enums stored as strings: `coverage_level`, `status`

### `insurance_claims`

- **PK**: `id`
- **FKs**: `billing_id -> billings.id`, `provider_id -> insurance_providers.id`, `policy_id -> patient_insurance_policies.id`, `plan_id -> insurance_plans.id`, `integration_partner_id -> integration_partners.id`
- **Fields**: `claim_number`, `status`, `submission_date`, `response_date`, `claimed_amount`, `approved_amount`, `patient_responsibility`, `rejection_reason`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `claim_number`
  - Enum stored as string: `status`

### `training_modules`

- **PK**: `id`
- **Fields**: `course_name`, `course_level`, `duration`, `rating`, `description`, `brochure_url`, `certification`, `enroll_now_available`, `enrolled_count`, `prerequisites`, `instructor_name`, `created_at`, `updated_at`, `price`, `max_enrollment`, `is_active`
- **Constraints/notes**:
  - Enum stored as string: `course_level`
  - Additional collection tables: `training_module_comments`, `training_module_modules`, `training_module_tags`

### `training_enrollments`

- **PK**: `id`
- **FKs**: `chw_id -> community_health_workers.id`, `training_module_id -> training_modules.id`
- **Fields**: `status`, `progress_percentage`, `completed_at`, `certificate_issued`, `notes`, `enrolled_at`, `updated_at`
- **Constraints/notes**:
  - Unique composite: (`chw_id`, `training_module_id`)
  - Enum stored as string: `status`

### `integration_partners`

- **PK**: `id`
- **Fields**: `name`, `type`, `api_url`, `api_key`, `contact_email`, `status`, `metadata`, `created_at`, `updated_at`
- **Constraints/notes**:
  - Unique: `name`
  - Enums stored as strings: `type`, `status`

### `audit_logs`

- **PK**: `id`
- **FKs**: `user_id -> users.id`
- **Fields**: `event_type`, `entity_type`, `entity_id`, `username`, `ip_address`, `event_time`, `details`, `status`, `error_message`, `integration_partner_id`, `session_id`, `correlation_id`, `user_agent`, `updated_at`
- **Constraints/notes**:
  - Enums stored as strings: `event_type`, `status`

## Secondary/collection tables from `@ElementCollection`

- `doctor_qualifications` (`doctor_id`, `qualification`)
- `doctor_languages` (`doctor_id`, `language`)
- `health_record_attachments` (`health_record_id`, attachment object columns)
- `prescription_side_effects` (`prescription_id`, `side_effect`)
- `pharmacy_services` (`pharmacy_id`, `service`)
- `session_symptoms` (`session_id`, `symptom`)
- `training_module_comments` (`training_module_id`, `comment`)
- `training_module_modules` (`training_module_id`, `module_name`)
- `training_module_tags` (`training_module_id`, `tag`)

## Additional entity tables present in the project

These are also modeled as JPA entities and can be added to this document in the same format when needed:

- `ambulances`, `ambulance_dispatches`, `ambulance_drivers`, `ambulance_equipment`, `ambulance_tracking`
- `chw_assignments`
- `reports`
- `service_orders`, `service_order_items`
- `specialties`
- `remittance_advices`

## Notable modeling caveat

- `UserRole` and `UserStatus` are currently enums annotated with `@Entity` and `@Table`. In typical JPA design, these should be plain enums (without `@Entity`) when stored via `@Enumerated(EnumType.STRING)` in `users`.

