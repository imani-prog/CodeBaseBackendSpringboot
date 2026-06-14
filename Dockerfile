# Multi-stage build for optimized Spring Boot application
FROM maven:3.9.9-amazoncorretto-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Runtime stage - smaller image
FROM amazoncorretto:17-alpine

# Add labels for metadata
LABEL maintainer="timothyimani128@gmail.com"
LABEL description="Medilink Spring Boot API"
LABEL version="1.0"

# Create app directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
