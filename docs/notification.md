# Notifications and Messaging over WebSockets (STOMP)

This document outlines how to add real-time notifications and 1:1/team messaging to the CodeBaseBackend using Spring WebSocket + STOMP, with JWT-based auth, offline persistence, delivery/read receipts, and a clean migration path. It includes concrete endpoints, message shapes, and client examples.

## Goals

- Real-time notifications for key domain events (e.g., ambulance dispatch created/updated, appointment changes, billing updates).
- 1:1 and small-group chat between Dispatchers and Ambulance crew.
- Authenticated sessions with role/tenant scoping.
- Offline-safe: missed items are persisted and delivered on reconnect via REST + sync.
- Delivery/read receipts for UX consistency.
- Scalable (single node → multi-node via Redis-backed broker).

## High-level Architecture

- Protocol: WebSocket with STOMP frames; SockJS fallback for older clients.
- Server: Spring Boot `spring-websocket` with message broker.
- Client: JS (`@stomp/stompjs` + `sockjs-client`) and Mobile (STOMP client or fallback push).
- Topics:
  - Broadcast topics (/topic/*) for event fanout (e.g., dispatch board updates).
  - User queues (/user/queue/*) for per-user notifications and messages.
- Persistence: JPA entities for Notification, Message, MessageThread; REST APIs to fetch, mark-read, paginate.
- Auth: JWT parsed at handshake; user identity bound to `Principal`; authorization enforced on message mappings and REST endpoints.

## Maven dependencies (pom.xml)

Add these (exact versions managed by Spring Boot parent):

- spring-boot-starter-websocket
- spring-boot-starter-security
- spring-boot-starter-validation
- spring-boot-starter-data-jpa (already present)
- spring-boot-starter-actuator (optional: metrics/health)
- spring-data-redis + lettuce/jedis (optional: for clustering)
- jackson-datatype-jsr310 (Java time)
- lombok (optional)

## WebSocket/STOMP Setup

- Endpoint: `/ws` (with SockJS). Example client URL: `wss://api.example.com/ws`.
- Application destination prefix: `/app` (client → server messages).
- Broker destination prefixes:
  - `/topic` for broadcast.
  - `/queue` for point-to-point; paired with `/user` prefix for per-user queues.
- User destination prefix: `/user` for `SimpMessagingTemplate.convertAndSendToUser`.

Example config shape (for reference, not implemented yet):

- register STOMP endpoint: `registry.addEndpoint("/ws").setAllowedOriginPatterns("*").addInterceptors(jwtHandshakeInterceptor).withSockJS()`
- set prefixes: `config.enableSimpleBroker("/topic", "/queue")` and `config.setApplicationDestinationPrefixes("/app"); config.setUserDestinationPrefix("/user")`
- heartbeats: `enableSimpleBroker().setHeartbeatValue(new long[]{10000, 10000})`

## Authentication and Authorization

- Handshake Interceptor: Extract and validate JWT from `Authorization: Bearer <token>` or `?access_token=`. On success, set `Principal` with `userId`, roles, hospital/tenant.
- Channel Interceptor: Enforce authorization on inbound messages (e.g., only DISPATCHER can send to ambulance-scoped destinations, only thread participants can send messages to that thread).
- Security guidelines:
  - Do not trust client-supplied user IDs in payloads; derive from `Principal`.
  - Validate path variables (e.g., ambulanceId belongs to user’s hospital).

## Domain Topics and Destinations

Server → Client (subscriptions):

- `/topic/dispatches` — Dispatch board updates (created/updated/cancelled).
- `/topic/ambulances/{ambulanceId}` — Broadcasts relevant to a single ambulance unit.
- `/user/queue/notifications` — Per-user notifications (bell icon feed).
- `/user/queue/messages` — Per-user message deliveries.
- Optional presence: `/topic/presence` (user online/offline), if needed.

Client → Server (send mappings under `/app`):

- `/app/messages.send` — Send a chat message; server persists and fanouts to recipients.
- `/app/notifications.ack` — Acknowledge delivery or mark-read for a notification.
- `/app/messages.ack` — Acknowledge delivery/read for a message.

## Message and Notification Payloads

Notification (server → client):

{
  "id": "uuid",
  "type": "DISPATCH|MESSAGE|SYSTEM",
  "title": "Ambulance Assigned",
  "body": "Ambulance A-12 dispatched to incident #8472",
  "entityType": "DISPATCH",
  "entityId": "8472",
  "metadata": { "priority": "HIGH", "ambulanceId": 12 },
  "createdAt": "2025-09-23T10:15:00Z",
  "deliveredAt": null,
  "readAt": null
}

Chat Message (server ↔ client):

{
  "id": "uuid",
  "threadId": "uuid",
  "clientMessageId": "uuid-generated-by-client",  // for idempotency
  "senderId": "user-123",
  "receiverId": "user-456",   // or participants[] for group
  "body": "ETA 5 minutes.",
  "attachments": [],
  "sentAt": "2025-09-23T10:15:00Z",
  "deliveredAt": null,
  "readAt": null,
  "status": "SENT|DELIVERED|READ"
}

Ack (client → server):

{
  "id": "uuid-of-item",
  "type": "NOTIFICATION|MESSAGE",
  "event": "DELIVERED|READ",
  "at": "2025-09-23T10:16:02Z"
}

## Persistence Model (proposed)

- Notification
  - id (UUID), userId, type, title, body, entityType, entityId, metadata (JSON), createdAt, deliveredAt, readAt, tenantId
- MessageThread
  - id (UUID), participants (userIds), createdAt, lastMessageAt, tenantId
- Message
  - id (UUID), threadId, senderId, body, attachments (JSON), clientMessageId, sentAt, deliveredAt, readAt, tenantId
- Indices: `(userId, readAt)`, `(threadId, sentAt DESC)`, `(tenantId, createdAt)`

## REST API (for offline sync and management)

- GET /api/notifications?since=…&page=…&size=… — Paginated unread + recent; filter by type.
- POST /api/notifications/mark-read — body: `{ ids: [..] }`.
- GET /api/messages/threads — list threads for user with last message preview.
- GET /api/messages/threads/{id}?before=…&size=… — paginate messages.
- POST /api/messages — send a new message (server also emits over WS).
- Optional: GET /api/ws/sessions — admin diagnostics.

These complement the WS channels: on reconnect, client fetches recent via REST to fill any gaps.

## Event Flow Examples (Ambulance Dispatch)

Dispatch created:

1) DispatchService creates a new dispatch and assigns ambulanceId.
2) Persist dispatch; publish domain event `DispatchCreated` (transactional outbox or `@TransactionalEventListener`).
3) WS layer receives event and:
   - Broadcasts a board update to `/topic/dispatches`.
   - Notifies the assigned ambulance crew via `/topic/ambulances/{ambulanceId}`.
   - Pushes a bell notification to dispatcher(s) via `/user/queue/notifications`.
4) Clients update UI; mobile can acknowledge delivery and mark read.

Chat between dispatcher and ambulance crew:

- Client sends to `/app/messages.send` with `threadId` or recipient; server validates participants, persists message, then emits to recipients’ `/user/queue/messages` and updates thread preview via `/topic/dispatches` if relevant.

## SimpMessagingTemplate Usage (server)

- Broadcast: `convertAndSend("/topic/dispatches", payload)`.
- Target user: `convertAndSendToUser(userId, "/queue/notifications", payload)`.
- Target ambulance topic: `convertAndSend("/topic/ambulances/" + ambulanceId, payload)`.

## Redis-backed Broker (Scaling)

For multi-instance deployment:

- Enable Spring’s built-in simple broker relay to Redis or use `enableStompBrokerRelay` to connect to an external broker (RabbitMQ STOMP or ActiveMQ). Redis pub/sub is often sufficient for horizontal scale with Spring’s `SimpMessagingTemplate`.
- Add spring-data-redis and configure `spring.redis.*` properties.

## Client Examples

Web (JS):

```js
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const token = 'JWT_HERE';
const client = new Client({
  webSocketFactory: () => new SockJS('/ws'),
  connectHeaders: { Authorization: `Bearer ${token}` },
  debug: (str) => console.log(str),
  reconnectDelay: 5000,
  heartbeatIncoming: 10000,
  heartbeatOutgoing: 10000,
});

client.onConnect = () => {
  client.subscribe('/topic/dispatches', (msg) => {
    const payload = JSON.parse(msg.body);
    // update board
  });
  client.subscribe('/user/queue/notifications', (msg) => {
    const n = JSON.parse(msg.body);
    // show bell notification
    // ack delivery
    client.publish({ destination: '/app/notifications.ack', body: JSON.stringify({ id: n.id, type: 'NOTIFICATION', event: 'DELIVERED', at: new Date().toISOString() }) });
  });
};

client.activate();
```

Android/Kotlin (concept):

- Use a STOMP client (e.g., `ua.naiksoftware:stomp`) or WebSocket with STOMP framing.
- On background/unreliable networks, use FCM push as a fallback to wake the app and then reconnect WS for state sync.

## Delivery Semantics and Idempotency

- Client includes `clientMessageId` when sending; server de-duplicates on `(senderId, clientMessageId)`.
- Server emits `id` for persisted items; client replaces temporary IDs on confirm.
- Use a small outbox on client to retry unsent messages on reconnect.
- Mark delivery/read via ack channels; server updates `deliveredAt`/`readAt` and optionally echoes receipts to other participants.

## Observability & Ops

- Metrics: active sessions, subscriptions, message rates, broker queues, delivery latency.
- Logs: connect/disconnect events, errors, authorization failures.
- Limits: max message size, rate limiting per user, attachment size via REST not WS.
- Backpressure: drop or buffer low-priority notifications when client is slow; always persist critical items.

## Security Considerations

- Validate all inputs; enforce roles (DISPATCHER, PARAMEDIC, ADMIN).
- Authorize by tenant/hospital on both REST and WS messages.
- Protect `/topic/ambulances/{id}` so only allowed users subscribe/send.
- Sanitize rendered content in clients; avoid HTML in messages.
- Data retention policies for messages/notifications; support deletion/anonymization.

## Testing Strategy

- Unit tests for mappers/validators.
- Integration test: spin up `WebSocketStompClient` against embedded server, perform connect, subscribe, send, assert broadcast and user-queue delivery.
- Load test: simulate N users with Gatling/K6 WebSocket support.

## Step-by-step Implementation Plan

1) Dependencies: add websocket, security, validation; optionally redis.
2) WebSocket config: endpoint `/ws`, prefixes, heartbeats, CORS.
3) Auth: JWT handshake + channel interceptor binding `Principal`.
4) DTOs: NotificationDto, MessageDto, AckDto (Jackson + jsr310 module).
5) Persistence: entities + repositories for Notification, Message, MessageThread.
6) Services: NotificationService, MessagingService (persist, query, mark-read, fanout).
7) Controllers:
   - STOMP @MessageMapping: `messages.send`, `notifications.ack`, `messages.ack`.
   - REST for listing notifications/messages and marking read.
8) Domain event wiring: from DispatchService, publish events → WS fanout.
9) Client integration: web/Android subscriptions; implement reconnection & offline sync.
10) Monitoring: metrics/logs; configure limits; add basic rate limiting.
11) Scale-out: enable Redis pub/sub and test multi-instance.

## How this fits our existing codebase

- A `NotificationController` class already exists; we can extend it to host:
  - `@MessageMapping` methods for acks and optionally for sending system notifications.
  - REST endpoints for list/mark-read operations.
- Dispatch-related controllers/services (e.g., `AmbulanceDispatchController`/service) can publish domain events; the Notification/Messaging services subscribe and fan out to WS.
- Use `SimpMessagingTemplate` for both broadcast (`/topic/*`) and per-user (`/user/queue/*`) messages.

## Cutover/Migration Tips

- Start with the SimpleBroker (in-memory) in dev; switch to Redis or broker relay before prod scale.
- Keep REST endpoints for offline sync from day one.
- Instrument early to understand subscription patterns and message volumes.

---

With the above, we can implement real-time notifications and messaging incrementally, keep it secure, and scale as needed without locking ourselves into vendor-specific brokers.

