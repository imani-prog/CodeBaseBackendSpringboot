# Notifications and Messaging using WebSockets

## Overview
WebSockets provide a full-duplex communication channel over a single TCP connection. They are ideal for real-time notifications and messaging systems. This document outlines the dependencies, setup, and implementation steps required to integrate WebSocket-based notifications and messaging into the application.

---

## Dependencies
To implement WebSocket-based notifications and messaging, the following dependencies are required:

### Maven Dependencies
Add the following dependencies to your `pom.xml` file:

```xml
<dependencies>
    <!-- Spring WebSocket -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-websocket</artifactId>
        <version>6.0.0</version> <!-- Use the latest compatible version -->
    </dependency>

    <!-- Spring Messaging -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-messaging</artifactId>
        <version>6.0.0</version>
    </dependency>

    <!-- STOMP Protocol Support -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
        <version>3.1.0</version>
    </dependency>

    <!-- Optional: Jackson for JSON serialization -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.0</version>
    </dependency>
</dependencies>
```

### Additional Dependencies
To enhance the notification and WebSocket functionality, consider the following additional dependencies:

```xml
<dependencies>
    <!-- Spring Security for securing WebSocket endpoints -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
        <version>3.1.0</version>
    </dependency>

    <!-- Spring Boot DevTools for easier development -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <version>3.1.0</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Optional: Lombok for reducing boilerplate code -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.28</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

---

## Implementation Steps

### 1. WebSocket Configuration
Create a configuration class to enable WebSocket support and define endpoint mappings. Example:

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }
}
```

### 2. Message Broker Setup
Use Spring's built-in STOMP broker to handle message routing.

### 3. Controller for WebSocket Endpoints
Create a controller to handle WebSocket connections and messaging. Example:

```java
@RestController
public class NotificationController {

    @MessageMapping("/sendNotification")
    @SendTo("/topic/notifications")
    public Notification sendNotification(Notification notification) {
        return notification;
    }
}
```

### 4. Frontend Integration
Use a WebSocket client library like SockJS or native WebSocket API to connect to the server. Example using JavaScript:

```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/notifications', function (notification) {
        console.log(JSON.parse(notification.body));
    });
});

function sendNotification(notification) {
    stompClient.send("/app/sendNotification", {}, JSON.stringify(notification));
}
```

### 5. Security
Secure WebSocket endpoints using Spring Security. Example configuration:

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/ws/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .csrf().disable()
            .headers().frameOptions().disable();
    }
}
```

---

## Example Use Cases
1. **Real-Time Notifications**: Notify users about ambulance dispatch updates.
2. **Messaging**: Enable chat functionality between hospital staff.

---

## Next Steps
1. Add the dependencies to the `pom.xml` file.
2. Implement the WebSocket configuration and controllers.
3. Test the WebSocket endpoints using a WebSocket client.
4. Document the API endpoints and usage instructions.

---

## Testing WebSocket Endpoints
Use tools like Postman or WebSocket clients to test the endpoints. Example test flow:
1. Connect to `/ws` endpoint.
2. Subscribe to `/topic/notifications`.
3. Send a message to `/app/sendNotification` and verify the broadcast.

---

## References
- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [STOMP Protocol](https://stomp.github.io/)
