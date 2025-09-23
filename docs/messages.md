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

## Database Storage for Messages

### Why Store Messages in Database Even with WebSockets?

**Yes, messages must be stored in the database even when using WebSockets.** Here's why:

1. **Persistence**: WebSockets only handle real-time delivery. If a user is offline, they won't receive the message.
2. **Message History**: Users need to see previous conversations and message history.
3. **Audit Trail**: For compliance and legal reasons, especially in healthcare.
4. **Reliability**: Network issues or server restarts could cause message loss without persistence.
5. **Multi-device Support**: Users can access messages from different devices.

### Database Schema

Create a `messages` table with the following structure:

```sql
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id VARCHAR(255) NOT NULL,
    sender_type ENUM('PATIENT', 'HOSPITAL', 'DISPATCHER', 'AMBULANCE') NOT NULL,
    receiver_id VARCHAR(255) NOT NULL,
    receiver_type ENUM('PATIENT', 'HOSPITAL', 'DISPATCHER', 'AMBULANCE') NOT NULL,
    content TEXT NOT NULL,
    message_type ENUM('TEXT', 'STATUS_UPDATE', 'NOTIFICATION', 'ALERT') DEFAULT 'TEXT',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_sender (sender_id, sender_type),
    INDEX idx_receiver (receiver_id, receiver_type),
    INDEX idx_created_at (created_at)
);
```

### Message Entity

```java
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sender_id", nullable = false)
    private String senderId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private UserType senderType;
    
    @Column(name = "receiver_id", nullable = false)
    private String receiverId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_type", nullable = false)
    private UserType receiverType;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType = MessageType.TEXT;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Getters and Setters
}

public enum UserType {
    PATIENT, HOSPITAL, DISPATCHER, AMBULANCE
}

public enum MessageType {
    TEXT, STATUS_UPDATE, NOTIFICATION, ALERT
}
```

### Updated Messaging Service

```java
@Service
@Transactional
public class MessagingService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public Message sendMessage(String senderId, UserType senderType, 
                              String receiverId, UserType receiverType, 
                              String content, MessageType messageType) {
        
        // 1. Save to database first
        Message message = new Message();
        message.setSenderId(senderId);
        message.setSenderType(senderType);
        message.setReceiverId(receiverId);
        message.setReceiverType(receiverType);
        message.setContent(content);
        message.setMessageType(messageType);
        
        Message savedMessage = messageRepository.save(message);
        
        // 2. Send via WebSocket for real-time delivery
        String destination = "/topic/messages/" + receiverId;
        messagingTemplate.convertAndSend(destination, savedMessage);
        
        return savedMessage;
    }
    
    public List<Message> getConversationHistory(String user1Id, UserType user1Type,
                                               String user2Id, UserType user2Type,
                                               Pageable pageable) {
        return messageRepository.findConversationBetweenUsers(
            user1Id, user1Type, user2Id, user2Type, pageable);
    }
    
    public List<Message> getUnreadMessages(String receiverId, UserType receiverType) {
        return messageRepository.findByReceiverIdAndReceiverTypeAndIsReadFalse(
            receiverId, receiverType);
    }
    
    public void markMessageAsRead(Long messageId) {
        messageRepository.updateReadStatus(messageId, true);
    }
}
```

### Message Repository

```java
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT m FROM Message m WHERE " +
           "((m.senderId = :user1Id AND m.senderType = :user1Type AND " +
           "m.receiverId = :user2Id AND m.receiverType = :user2Type) OR " +
           "(m.senderId = :user2Id AND m.senderType = :user2Type AND " +
           "m.receiverId = :user1Id AND m.receiverType = :user1Type)) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findConversationBetweenUsers(
        @Param("user1Id") String user1Id, 
        @Param("user1Type") UserType user1Type,
        @Param("user2Id") String user2Id, 
        @Param("user2Type") UserType user2Type,
        Pageable pageable);
    
    List<Message> findByReceiverIdAndReceiverTypeAndIsReadFalse(
        String receiverId, UserType receiverType);
    
    @Modifying
    @Query("UPDATE Message m SET m.isRead = :isRead WHERE m.id = :messageId")
    void updateReadStatus(@Param("messageId") Long messageId, 
                         @Param("isRead") Boolean isRead);
}
```

### Hybrid Approach: WebSocket + Database

The recommended approach is:

1. **Save First**: Always save the message to the database first for persistence.
2. **Send via WebSocket**: Then send the message via WebSocket for real-time delivery.
3. **Fallback Mechanism**: If WebSocket delivery fails, the message is still stored and can be retrieved later.
4. **Offline Support**: When users come online, they can fetch missed messages from the database.

### Message Delivery Flow

```java
@RestController
@RequestMapping("/api/messages")
public class MessagingController {
    
    @Autowired
    private MessagingService messagingService;
    
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody SendMessageRequest request) {
        Message savedMessage = messagingService.sendMessage(
            request.getSenderId(),
            request.getSenderType(),
            request.getReceiverId(),
            request.getReceiverType(),
            request.getContent(),
            request.getMessageType()
        );
        
        return ResponseEntity.ok(savedMessage);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<Message>> getMessageHistory(
            @RequestParam String user1Id,
            @RequestParam UserType user1Type,
            @RequestParam String user2Id,
            @RequestParam UserType user2Type,
            @PageableDefault(size = 50) Pageable pageable) {
        
        List<Message> messages = messagingService.getConversationHistory(
            user1Id, user1Type, user2Id, user2Type, pageable);
        
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<Message>> getUnreadMessages(
            @RequestParam String receiverId,
            @RequestParam UserType receiverType) {
        
        List<Message> unreadMessages = messagingService.getUnreadMessages(
            receiverId, receiverType);
        
        return ResponseEntity.ok(unreadMessages);
    }
    
    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        messagingService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }
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
