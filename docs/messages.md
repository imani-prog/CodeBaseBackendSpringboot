# Messaging in the Application

## Overview
Messaging functionality in the application can be implemented to facilitate communication between various entities. This document outlines how messaging can be integrated into the application, focusing on its relation to the existing entities and the steps required for implementation.

---

## Entities and Their Role in Messaging

### 1. **Ambulances**
- **Use Case**: Ambulances can send status updates (e.g., "en route", "arrived", "unavailable") to the dispatch center or hospital staff.
- **Messaging Flow**:
  - Ambulance sends a message to the dispatch center.
  - Dispatch center broadcasts the update to relevant hospital staff.

### 2. **Ambulance Dispatchers**
- **Use Case**: Dispatchers can communicate with ambulance drivers and hospital staff to coordinate operations.
- **Messaging Flow**:
  - Dispatchers send instructions to ambulances.
  - Dispatchers receive updates from ambulances and notify hospitals.

### 3. **Hospitals**
- **Use Case**: Hospitals can send requests for ambulances and receive updates about their status.
- **Messaging Flow**:
  - Hospitals send a request to the dispatch center.
  - Dispatch center forwards the request to available ambulances.

### 4. **Patients**
- **Use Case**: Patients can receive notifications about ambulance arrival times or appointment reminders.
- **Messaging Flow**:
  - Dispatch center sends updates to patients.
  - Patients can acknowledge receipt of updates.

### 5. **Service Orders**
- **Use Case**: Service orders can trigger notifications to relevant entities (e.g., ambulance dispatchers, hospitals).
- **Messaging Flow**:
  - Service order creation triggers a message to the dispatch center.
  - Dispatch center routes the message to the appropriate entities.

---

## Implementation Steps

### 1. Define Message Structure
Create a common message structure to standardize communication between entities. Example:

```java
public class Message {
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timestamp;

    // Getters and Setters
}
```

### 2. WebSocket Integration
Use WebSockets to enable real-time messaging. Refer to the `notification.md` file for WebSocket setup.

### 3. Messaging Service
Implement a messaging service to handle message routing and delivery. Example:

```java
@Service
public class MessagingService {

    public void sendMessage(String sender, String receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        // Logic to deliver the message
    }
}
```

### 4. Controller for Messaging Endpoints
Create a controller to expose messaging endpoints. Example:

```java
@RestController
@RequestMapping("/messages")
public class MessagingController {

    @Autowired
    private MessagingService messagingService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Message message) {
        messagingService.sendMessage(message.getSender(), message.getReceiver(), message.getContent());
        return ResponseEntity.ok("Message sent successfully");
    }
}
```

### 5. Frontend Integration
Use WebSocket or REST API to send and receive messages. Example using JavaScript:

```javascript
function sendMessage(sender, receiver, content) {
    const message = {
        sender: sender,
        receiver: receiver,
        content: content,
        timestamp: new Date().toISOString()
    };

    fetch('/messages/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(message)
    }).then(response => {
        if (response.ok) {
            console.log('Message sent successfully');
        }
    });
}
```

---

## Security Considerations
1. **Authentication**: Ensure only authenticated users can send and receive messages.
2. **Authorization**: Restrict messaging to relevant entities (e.g., a patient cannot message another patient).
3. **Encryption**: Use HTTPS and secure WebSocket (wss://) to encrypt messages in transit.

---

## Example Use Cases
1. **Ambulance Status Updates**: Ambulances send real-time updates to dispatchers and hospitals.
2. **Patient Notifications**: Notify patients about ambulance arrival times or appointment reminders.
3. **Hospital Requests**: Hospitals request ambulances and receive updates on their status.

---

## Next Steps
1. Define the message structure in the codebase.
2. Implement the messaging service and controller.
3. Test the messaging functionality using WebSocket clients and REST API tools.
4. Document the API endpoints and usage instructions.

---

## References
- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [Spring Messaging](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#messaging)
