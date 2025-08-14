# Billing Module Documentation

This guide covers the Billing domain: entities, repositories, services, controllers, workflows, validation, and extensibility.

## Overview
Billing manages invoices for services, payments against invoices, balances, and invoice state. It supports cash/card/mobile money out-of-pocket and lays the ground for insurance-backed reconciliation.

Core classes:
- Entity: Billing, Payment
- Repository: BillingRepository, PaymentRepository
- Service: BillingService(+Impl), PaymentService(+Impl)
- Controller: BillingController, PaymentController

## Entities

### Billing
Table: billings

Key fields
- id: Long (PK)
- invoiceNumber: String (unique, indexed)
- patient: ManyToOne Patient (required)
- hospital: ManyToOne Hospital (optional)
- issueDate, serviceDate, dueDate: OffsetDateTime
- subtotal, discount, tax, total: BigDecimal (14,2)
- amountPaid, balance: BigDecimal (14,2)
- currency: String(3), default KES
- status: InvoiceStatus (DRAFT, ISSUED, PARTIALLY_PAID, PAID, CANCELED, WRITEOFF)
- notes: Text
- payments: OneToMany Payment
- insuranceClaims: OneToMany InsuranceClaim
- createdAt, updatedAt: timestamps

Indexes/uniques
- invoiceNumber unique
- patient_id, hospital_id, status indexed

Defaults (@PrePersist)
- issueDate = now
- status = ISSUED
- currency = KES
- monetary fields default to 0
- balance = total − amountPaid

Status transitions
- New invoices: ISSUED
- When COMPLETED payment posted:
  - amountPaid += payment.amount
  - balance = total − amountPaid
  - if balance == 0 -> PAID; else PARTIALLY_PAID
- DRAFT/CANCELED/WRITEOFF managed by service rules UI

### Payment
Table: payments

Key fields
- id: Long (PK)
- billing: ManyToOne Billing (required)
- method: Method (CASH, CARD, MOBILE_MONEY, INSURANCE)
- status: Status (PENDING, COMPLETED, FAILED, REFUNDED)
- amount: BigDecimal (14,2) > 0
- currency: String(3)
- externalReference: String(64) (gateway txn id or insurer reference)
- notes: Text
- createdAt, updatedAt: timestamps

Indexes
- billing_id, method, status

Behavior
- When saved as COMPLETED, Billing aggregates are updated (amountPaid, balance, status).

## Repositories

### BillingRepository
- Optional<Billing> findByInvoiceNumber(String invoiceNumber)
- List<Billing> findByPatientIdOrderByIssueDateDesc(Long patientId)
- List<Billing> findByStatusOrderByIssueDateDesc(Billing.InvoiceStatus status)

### PaymentRepository
- List<Payment> findByBillingIdOrderByCreatedAtAsc(Long billingId)

## Services

### BillingService
- BillingResponse create(BillingRequest request)
- BillingResponse get(Long id)
- BillingResponse getByInvoice(String invoiceNumber)
- BillingResponse update(Long id, BillingRequest request)
- void delete(Long id)
- List<BillingResponse> listByPatient(Long patientId)
- List<BillingResponse> listByStatus(InvoiceStatus status)

Notes
- Invoice number auto-generates as INV-<random> if omitted.
- Currency defaults to KES unless provided.
- Recomputes balance when total changes.

### PaymentService
- PaymentResponse record(PaymentRequest request)
- List<PaymentResponse> listByBilling(Long billingId)

Notes
- Validates amount > 0.
- Updates Billing aggregate on COMPLETED payments.

## Controllers and Endpoints

### BillingController (/api/billing)
- POST /            → create invoice
- GET /{id}         → get invoice by id
- GET /invoice/{no} → get by invoiceNumber
- PUT /{id}         → update invoice
- DELETE /{id}      → delete invoice
- GET /patient/{id} → list invoices for patient (desc by issueDate)
- GET /status/{st}  → list by status

Sample create request (BillingRequest)
```json
{
  "patientId": 1,
  "hospitalId": 2,
  "invoiceNumber": "INV-2025-0001",
  "serviceDate": "2025-08-14T08:00:00Z",
  "dueDate": "2025-09-01T00:00:00Z",
  "subtotal": 2000.00,
  "discount": 0,
  "tax": 0,
  "total": 2000.00,
  "currency": "KES",
  "status": "ISSUED",
  "notes": "Consultation and lab"
}
```

BillingResponse includes computed balance, timestamps, and status.

### PaymentController (/api/payments)
- POST /                  → record payment
- GET /billing/{billingId}→ list payments for invoice

Sample payment request (PaymentRequest)
```json
{
  "billingId": 10,
  "method": "MOBILE_MONEY",
  "status": "COMPLETED",
  "amount": 2000.00,
  "currency": "KES",
  "externalReference": "MPESA-XYZ123",
  "notes": "Till 123456"
}
```

PaymentResponse shows saved payment and timestamps.

## Workflows

- Create invoice → issueDate set, totals set, balance = total
- Post COMPLETED payment → billing.amountPaid += amount; billing.balance, status updated
- Query invoices by patient or status for follow-up/reconciliation

## Validation & Error Handling
- Monetary fields must be non-negative (amount > 0 for payments)
- 404 for missing Patient/Hospital/Billing
- 400 for invalid enum names or bad amounts

## Security & Auditing
- Protect endpoints by role (e.g., BILLING_VIEW, BILLING_EDIT)
- Add AuditLog entries for create/update/delete (recommended next step)

## Extensibility
- Line items: add BillingItem entity if detailed service lines are needed
- Taxes/discounts: move to rule-based calculator service
- Ledger: post GL entries on payment & write-offs
- Multi-currency: validate ISO 4217 and FX conversions

## Known Extensions
- Auto-reconciliation from RemittanceAdvice (insurer payment) → convert to Payment with method=INSURANCE
- Dunning reminders for overdue balances

---

