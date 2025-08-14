# Insurance Module Documentation

This guide documents the Insurance domain: providers, plans, patient policies, claims, remittances, and their repositories, services, controllers, and workflows.

## Overview
The insurance module supports managing insurers and plans, capturing patient policies, submitting and tracking claims, and recording remittance advice. It complements Billing for end-to-end invoicing and reconciliation.

Core classes:
- Entities: InsuranceProvider, InsurancePlan, PatientInsurancePolicy, InsuranceClaim, RemittanceAdvice
- Repositories: InsuranceProviderRepository, InsurancePlanRepository, PatientInsurancePolicyRepository, InsuranceClaimRepository, RemittanceAdviceRepository
- Services: InsuranceService(+Impl) [providers, plans, policies], InsuranceClaimService(+Impl), RemittanceAdviceService(+Impl)
- Controllers: InsuranceProviderController, InsurancePlanController, PatientInsurancePolicyController, InsuranceClaimController, RemittanceAdviceController

## Entities

### InsuranceProvider
Table: insurance_providers

Key fields
- id, name (unique), payerId (unique), registrationNumber
- contact: email, phone, fax, website
- portals: providerPortalUrl, claimsSubmissionUrl, claimsSubmissionEmail, supportPhone, supportEmail
- address: addressLine1/2, city, state, postalCode, country
- status: ProviderStatus (ACTIVE, INACTIVE)
- notes
- plans: OneToMany InsurancePlan
- policies: OneToMany PatientInsurancePolicy
- createdAt, updatedAt

Indexes/uniques
- name unique, payerId unique; name and payerId indexed

### InsurancePlan
Table: insurance_plans

Key fields
- id
- provider: ManyToOne InsuranceProvider (required)
- planName, planCode (provider+planCode unique)
- planType: HMO, PPO, EPO, POS, MEDICARE, MEDICAID, SELF_PAY, OTHER
- networkType: IN_NETWORK, OUT_OF_NETWORK, BOTH
- coverageDetails: text
- deductibleIndividual/family, oopMaxIndividual/family: BigDecimal(12,2)
- copayPrimaryCare/specialist/emergency: BigDecimal(12,2)
- coinsurancePercent: 0–100
- requiresReferral, preauthRequired
- effectiveFrom/effectiveTo
- status: PlanStatus (ACTIVE, INACTIVE)
- notes
- createdAt, updatedAt

Indexes/uniques
- provider_id indexed; (provider_id, planCode) unique

### PatientInsurancePolicy
Table: patient_insurance_policies

Key fields
- id
- patient: ManyToOne Patient (required)
- provider: ManyToOne InsuranceProvider (required)
- plan: ManyToOne InsurancePlan (optional)
- memberId (with provider unique)
- groupNumber
- coverageLevel: PRIMARY, SECONDARY, TERTIARY
- policyholderName, policyholderRelation, policyholderDob
- effectiveFrom (required), effectiveTo
- status: PolicyStatus (ACTIVE, INACTIVE, TERMINATED)
- cardFrontUrl, cardBackUrl, notes
- createdAt, updatedAt

Indexes/uniques
- patient_id, provider_id, memberId indexed; (provider_id, memberId) unique

### InsuranceClaim
Table: insurance_claims

Key fields
- id
- billing: ManyToOne Billing (required)
- provider: ManyToOne InsuranceProvider (required)
- policy: ManyToOne PatientInsurancePolicy (optional)
- plan: ManyToOne InsurancePlan (optional)
- claimNumber (unique, optional)
- status: ClaimStatus (DRAFT, SUBMITTED, PENDING, APPROVED, REJECTED, PAID, CANCELED)
- submissionDate, responseDate
- claimedAmount, approvedAmount, patientResponsibility (BigDecimal 14,2)
- rejectionReason
- remittances: OneToMany RemittanceAdvice
- createdAt, updatedAt

Indexes/uniques
- billing_id, provider_id, status indexed; claimNumber unique

### RemittanceAdvice
Table: remittance_advices

Key fields
- id
- claim: ManyToOne InsuranceClaim (required)
- amountPaid: BigDecimal(14,2)
- payerReference: String(64)
- remittanceDate: OffsetDateTime
- adjustments: text (free-form/JSON)
- notes: text
- createdAt, updatedAt

Indexes
- claim_id, remittanceDate

## Repositories

- InsuranceProviderRepository
  - Optional<InsuranceProvider> findByNameIgnoreCase(String name)
  - Optional<InsuranceProvider> findByPayerId(String payerId)
  - List<InsuranceProvider> findByStatus(ProviderStatus status)
  - List<InsuranceProvider> findByNameContainingIgnoreCase(String partial)
- InsurancePlanRepository
  - List<InsurancePlan> findByProviderId(Long providerId)
  - List<InsurancePlan> findByProviderIdAndStatusOrderByPlanNameAsc(Long providerId, PlanStatus status)
- PatientInsurancePolicyRepository
  - List<PatientInsurancePolicy> findByPatientId(Long patientId)
  - List<PatientInsurancePolicy> findByProviderId(Long providerId)
  - List<PatientInsurancePolicy> findByPatientIdAndStatus(Long patientId, PolicyStatus status)
  - Optional<PatientInsurancePolicy> findByProviderIdAndMemberId(Long providerId, String memberId)
  - List<PatientInsurancePolicy> findByPatientIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(Long patientId, LocalDate from, LocalDate to)
- InsuranceClaimRepository
  - Optional<InsuranceClaim> findByClaimNumber(String claimNumber)
  - List<InsuranceClaim> findByBillingId(Long billingId)
  - List<InsuranceClaim> findByProviderIdAndStatus(Long providerId, ClaimStatus status)
- RemittanceAdviceRepository
  - List<RemittanceAdvice> findByClaimId(Long claimId)

## Services

### InsuranceService
Groups provider, plan, and policy operations.

Providers
- InsuranceProviderResponse createProvider(InsuranceProviderRequest)
- getProvider, updateProvider, deleteProvider
- listProvidersByStatus, searchProvidersByName

Plans
- InsurancePlanResponse createPlan(InsurancePlanRequest)
- getPlan, updatePlan, deletePlan
- listPlansByProvider, listPlansByProviderAndStatus

Policies
- PatientInsurancePolicyResponse createPolicy(PatientInsurancePolicyRequest)
- getPolicy, updatePolicy, deletePolicy
- listPoliciesByPatient, listPoliciesByProvider
- listActivePoliciesForPatientInRange(patientId, from, to)

Validation highlights
- Ensures provider/patient/plan existence
- Enum parsing guards invalid values with 400 responses
- Policy effectiveFrom required; coverageLevel/status defaults

### InsuranceClaimService
- InsuranceClaimResponse create(InsuranceClaimRequest)
- get, updateStatus(id, status, reason)
- listByBilling, listByProviderAndStatus

Notes
- Validates required Billing and Provider
- Optionally associates Policy/Plan
- Sets submissionDate on creation; sets responseDate on status updates

### RemittanceAdviceService
- RemittanceAdviceResponse create(RemittanceAdviceRequest)
- listByClaim

Notes
- Associates to a claim; does not mutate billing by itself (recommended: apply reconciliation to Billing/Payments based on insurer advice).

## Controllers and Endpoints

### InsuranceProviderController (/api/insurance/providers)
- POST /         → create provider
- GET /{id}      → get provider
- PUT /{id}      → update
- DELETE /{id}   → delete
- GET /?status=ACTIVE or ?q=term → list by status or fuzzy name

### InsurancePlanController (/api/insurance/plans)
- POST /
- GET /{id}
- PUT /{id}
- DELETE /{id}
- GET /provider/{providerId}
- GET /provider/{providerId}/status/{status}

### PatientInsurancePolicyController (/api/insurance/policies)
- POST /
- GET /{id}
- PUT /{id}
- DELETE /{id}
- GET /patient/{patientId}
- GET /provider/{providerId}
- GET /active?patientId=..&from=YYYY-MM-DD&to=YYYY-MM-DD

### InsuranceClaimController (/api/claims)
- POST /
- GET /{id}
- PATCH /{id}/status?status=APPROVED&reason=...
- GET /billing/{billingId}
- GET /provider/{providerId}/status/{status}

### RemittanceAdviceController (/api/remittances)
- POST /
- GET /claim/{claimId}

## Typical Workflows

Eligibility (future integration)
- Use provider/policy to call external eligibility API (NHIF/SHA/private)
- Cache results with TTL, store audit

Claim submission
- Build claim from Billing and Policy; set status=SUBMITTED
- Track status via polling or webhooks; update status and responseDate

Remittance & reconciliation
- Record RemittanceAdvice upon insurer payment
- Optional: apply reconciliation to Billing:
  - Set InsuranceClaim.approvedAmount/patientResponsibility
  - Post a Payment (method=INSURANCE) for amountPaid
  - Update Billing.amountPaid/balance/status

## Validation & Error Handling
- 404 for missing references (provider, plan, policy, billing)
- 400 for invalid enum names or missing required fields
- Defensive defaults for enums and timestamps

## Security & Compliance
- Protect endpoints by role (e.g., INSURANCE_VIEW, INSURANCE_EDIT, CLAIM_SUBMIT)
- Log audit events for create/update/delete and external API interactions
- Obfuscate sensitive policy/member details in logs

## Extensibility
- Plug-in adapters for insurer APIs (NHIF/SHA/private) with provider-specific configs
- Map to FHIR Claim/ExplanationOfBenefit resources for interoperability
- Add prior authorizations and referrals workflow
- Webhooks for status updates and remittance notifications

## Sample Requests

Create provider
```json
{
  "name": "NHIF",
  "payerId": "NHIF-KE",
  "email": "support@nhif.go.ke",
  "status": "ACTIVE"
}
```

Create plan
```json
{
  "providerId": 1,
  "planName": "In-Patient",
  "planCode": "IP-01",
  "planType": "OTHER",
  "networkType": "BOTH",
  "status": "ACTIVE"
}
```

Create policy
```json
{
  "patientId": 10,
  "providerId": 1,
  "planId": 2,
  "memberId": "ABC123456",
  "coverageLevel": "PRIMARY",
  "effectiveFrom": "2025-01-01",
  "status": "ACTIVE"
}
```

Create claim
```json
{
  "billingId": 100,
  "providerId": 1,
  "policyId": 20,
  "planId": 2,
  "status": "SUBMITTED",
  "claimedAmount": 5000.00
}
```

Post remittance
```json
{
  "claimId": 200,
  "amountPaid": 4500.00,
  "payerReference": "NHIF-REM-2025-0001",
  "remittanceDate": "2025-08-15T10:00:00Z"
}
```

---

