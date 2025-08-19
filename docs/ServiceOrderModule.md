# Service Ordering & Billing Integration Guide

This document explains the end-to-end Service Ordering capability enabling patients to order services online and pay via the existing Billing/Payment modules. It covers the domain model, API contracts, validations, lifecycle, auditing, and examples.

---

## Overview

Goal: Allow a patient to place a service order (with one or more line items), automatically generate an invoice (Billing), and pay it using the existing Payment API. The order’s status reflects invoice payment state.

Key features:
- ServiceOrder and ServiceOrderItem entities with totals and currency.
- Optional hospital association on orders.
- Automatic Billing invoice creation when an order is placed.
- Payments update invoice totals and set ServiceOrder to PAID when fully settled.
- Auditing on core endpoints for traceability.

---

## High-level Flow

1) Patient places an order with one or more services (items).
2) Backend validates and stores the ServiceOrder and items.
3) Backend creates a Billing invoice with derived totals and links it to the order.
4) Patient pays the invoice via /api/payments (existing module).
5) When the invoice becomes PAID, the linked ServiceOrder status is set to PAID.

Sequence (simplified):
```
Client → POST /api/orders → ServiceOrderService.placeOrder → save order
      → BillingService.create → create invoice → link to order → INVOICED
Client → POST /api/payments (COMPLETED) → update invoice totals/status
      → if invoice PAID → mark ServiceOrder PAID
```

---

## Domain Model

### ServiceOrder (Entity)
- Table: service_orders
- Fields (main):
  - id: Long (PK)
  - patient: Patient (required)
  - hospital: Hospital (optional)
  - status: enum [PENDING, INVOICED, PAID, CANCELED] (default PENDING)
  - subtotal, discount, tax, total: BigDecimal (defaults to 0)
  - currency: 3-letter code (default KES)
  - notes: text
  - billing: Billing reference (set after invoice creation)
  - createdAt, updatedAt: timestamps
- Indexes:
  - patient_id, hospital_id, status

### ServiceOrderItem (Entity)
- Table: service_order_items
- Fields:
  - id: Long (PK)
  - order: ServiceOrder (required)
  - serviceName: String (required)
  - serviceCode: String (optional)
  - quantity: Integer (>= 1)
  - unitPrice: BigDecimal (>= 0)
  - lineSubtotal = quantity × unitPrice (computed)
  - lineTax: BigDecimal (>= 0; optional)
  - lineTotal = lineSubtotal + lineTax (computed)

### Billing (Existing)
- An invoice is created from an order using BillingService.create().
- Relevant fields: invoiceNumber, totals (subtotal, discount, tax, total), amountPaid, balance, currency, status [ISSUED, PARTIALLY_PAID, PAID, ...].

### Payment (Existing)
- Payments are recorded against a Billing id.
- Method enum: CASH, CARD, MOBILE_MONEY, INSURANCE
- Status enum: PENDING, COMPLETED, FAILED, REFUNDED

---

## Status Lifecycle

- Order: PENDING → INVOICED → PAID → (optional CANCELED)
  - PENDING: transient before invoice is created (typically very brief).
  - INVOICED: set immediately after invoice creation and linkage.
  - PAID: set when linked invoice reaches PAID status.
  - CANCELED: future use (not yet wired to an endpoint).

- Invoice (Billing): ISSUED → PARTIALLY_PAID → PAID (or CANCELED/WRITEOFF per module rules)

---

## APIs

### 1) Service Orders

Base path: /api/orders

- POST /api/orders → place order (creates ServiceOrder + items, auto-creates invoice)
- GET /api/orders/{id} → fetch a single order
- GET /api/orders/patient/{patientId} → list patient’s orders (newest first)

Auditing:
- POST: CREATE with includeArgs/includeResult.
- GET: READ with id/patientId entityIdExpression.

#### Request: POST /api/orders
Content-Type: application/json

Example
```json
{
  "patientId": 123,
  "hospitalId": 5,
  "currency": "KES",
  "notes": "Home sample collection",
  "discount": 100.00,
  "items": [
    {
      "serviceName": "Laboratory Test - CBC",
      "serviceCode": "LAB-CBC",
      "quantity": 1,
      "unitPrice": 1500.00,
      "lineTax": 0.00
    },
    {
      "serviceName": "Consultation",
      "serviceCode": "CONSULT",
      "quantity": 1,
      "unitPrice": 1000.00,
      "lineTax": 0.00
    }
  ]
}
```

Validation & Rules
- patientId: must exist.
- hospitalId: optional; if provided must exist.
- items: at least one item.
- quantity: >= 1; unitPrice: >= 0; lineTax: >= 0.
- currency: optional, default KES.
- Totals: subtotal=sum(lineSubtotal), tax=(request.tax or sum lineTax), total=subtotal − discount + tax, floored at 0.

Response: 200 OK
```json
{
  "id": 456,
  "patientId": 123,
  "hospitalId": 5,
  "status": "INVOICED",
  "subtotal": 2500.00,
  "discount": 100.00,
  "tax": 0.00,
  "total": 2400.00,
  "currency": "KES",
  "notes": "Home sample collection",
  "billingId": 789,
  "invoiceNumber": "INV-ABC1234567",
  "createdAt": "2025-08-19T10:15:00Z",
  "updatedAt": "2025-08-19T10:15:00Z",
  "items": [
    {
      "id": 1,
      "serviceName": "Laboratory Test - CBC",
      "serviceCode": "LAB-CBC",
      "quantity": 1,
      "unitPrice": 1500.00,
      "lineSubtotal": 1500.00,
      "lineTax": 0.00,
      "lineTotal": 1500.00
    },
    {
      "id": 2,
      "serviceName": "Consultation",
      "serviceCode": "CONSULT",
      "quantity": 1,
      "unitPrice": 1000.00,
      "lineSubtotal": 1000.00,
      "lineTax": 0.00,
      "lineTotal": 1000.00
    }
  ]
}
```

Errors
- 400 Bad Request: missing/invalid patientId, invalid hospitalId, empty items, invalid quantity/unitPrice.
- 404 Not Found: patient or hospital not found.

#### GET /api/orders/{id}
- Returns a ServiceOrderResponse with items and billing linkage; 404 if missing.

#### GET /api/orders/patient/{patientId}
- Returns a list of ServiceOrderResponse (newest first); 200 with empty array if none.

### 2) Payments (existing)

Base path: /api/payments

- POST /api/payments → record a payment against an invoice.
- GET /api/payments/billing/{billingId} → list payments for an invoice.

Request: POST /api/payments
```json
{
  "billingId": 789,
  "method": "MOBILE_MONEY",
  "status": "COMPLETED",
  "amount": 2400.00,
  "currency": "KES",
  "externalReference": "MPESA-XYZ123",
  "notes": "Order 456 payment"
}
```

Rules
- billingId must exist.
- amount > 0.
- method in [CASH, CARD, MOBILE_MONEY, INSURANCE].
- status in [PENDING, COMPLETED, FAILED, REFUNDED]. Only COMPLETED updates invoice totals.

Effects
- On COMPLETED: invoice amountPaid and balance updated, status set to PARTIALLY_PAID or PAID.
- If invoice becomes PAID: linked ServiceOrder.status becomes PAID.

Response: 200 OK (PaymentResponse)

---

## Integration Details

### Automatic Invoice Creation
When a ServiceOrder is placed, the service builds a BillingRequest using order totals and currency, calls BillingService.create(), then links the created invoice back to the order and sets status=INVOICED.

### Order Status Sync on Payment
PaymentService updates invoice aggregates. If invoice transitions to PAID, it locates the ServiceOrder by billingId and sets status=PAID.

### Auditing
- Annotations: @Auditable on order endpoints.
- POST /api/orders logs CREATE with includeArgs/includeResult; entityIdExpression uses #result.body.id.
- GET endpoints log READ with entity id or patientId.

---

## Validation Matrix

- ServiceOrderRequest
  - patientId: required, must exist.
  - hospitalId: optional; if provided, must exist.
  - items: required, size >= 1.
  - currency: optional; default KES.
  - discount/tax: optional; non-negative.
- ServiceOrderItemRequest
  - serviceName: required.
  - quantity: >= 1.
  - unitPrice: >= 0.
  - lineTax: >= 0 (optional).
- PaymentRequest
  - billingId: required; must exist.
  - amount: > 0.
  - method/status: valid enums.

---

## Error Handling

- 400 Bad Request: invalid input, non-existent hospitalId on order, duplicate/invalid values.
- 404 Not Found: patient/hospital/order/invoice missing.
- 500 errors are avoided for FK violations by validating IDs prior to persistence.

---

## Security & Multi-Tenancy (Assumptions)

- Authentication/authorization are assumed handled globally (not shown here).
- If you scope patients/orders by hospital/tenant, ensure repository methods are constrained appropriately.
- Sensitive PII is confined to core entities; auditing includes input args and results by design—redact or reduce if needed.

---

## Configuration Defaults

- Currency defaults to KES.
- Billing invoiceNumber is auto-generated when not provided (e.g., INV-XXXXXXXXXX).
- Invoice dueDate currently set to serviceDate + 14 days in order flow.

---

## Examples (Quick Start)

1) Place an order (hospital optional)
```http
POST /api/orders
Content-Type: application/json

{
  "patientId": 123,
  "currency": "KES",
  "items": [
    { "serviceName": "X-Ray Chest", "quantity": 1, "unitPrice": 2000.00 }
  ]
}
```

2) Pay the invoice (COMPLETED to settle and mark order PAID if fully covered)
```http
POST /api/payments
Content-Type: application/json

{
  "billingId": 789,
  "method": "MOBILE_MONEY",
  "status": "COMPLETED",
  "amount": 2000.00
}
```

3) Get the order
```http
GET /api/orders/456
Accept: application/json
```

4) List a patient’s orders
```http
GET /api/orders/patient/123
Accept: application/json
```

---

## Implementation Notes

- Entities:
  - com.example.codebasebackend.Entities.ServiceOrder
  - com.example.codebasebackend.Entities.ServiceOrderItem
- Repository:
  - ServiceOrderRepository (findByPatientIdOrderByCreatedAtDesc, findByBillingId)
- Service:
  - ServiceOrderService, ServiceOrderServiceImplementation (placeOrder, get, listByPatient)
  - PaymentServiceImplementation updated to sync order status on invoice PAID
- Controller:
  - ServiceOrderController (POST, GET by id, GET by patient)
- DTOs:
  - ServiceOrderRequest, ServiceOrderItemRequest
  - ServiceOrderResponse, ServiceOrderItemResponse

---

## Edge Cases & Considerations

- Invalid hospitalId: return 400 instead of 500 FK violation.
- Totals with discounts: ensure total doesn’t go below 0.
- Mixed tax handling: supports per-line tax or order-level tax (if provided).
- Partial payments: order stays INVOICED until invoice fully paid.
- Currency consistency: payment currency defaults to invoice currency if omitted.

---

## Roadmap / Next Steps

- Admin service catalog (codes, pricing, tax rules) with versioning.
- Quoting vs ordering (DRAFT orders, confirm to INVOICED).
- Coupons/advanced discount strategies and tax calculations.
- Refunds and order status transitions on refund/chargeback.
- Notifications (email/SMS) for order creation and payment confirmation.
- Patient portal UI/UX and secure payment gateway integration.
- Reporting: revenue by service, patient order history, unpaid invoices.

---

## Changelog (Module-related)

- Added ServiceOrder + ServiceOrderItem entities with JPA mappings and indexes.
- Added ServiceOrderRepository / ServiceOrderService / ServiceOrderController.
- Integrated BillingService.create() during order placement.
- Updated PaymentServiceImplementation to mark Order as PAID when invoice is PAID.
- Added auditing annotations to ServiceOrder endpoints.

---

## Contact

Module maintainer: Backend Team
Related docs: docs/BillingModule.md, docs/PatientEntity.md, docs/HospitalEntity.md

