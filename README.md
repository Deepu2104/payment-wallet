# PayWallet Core

A production-grade digital wallet system built with Spring Boot, PostgreSQL, Kafka, and Redis.

## Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

## getting Started

1. **Start Infrastructure**
   ```bash
   docker-compose up -d
   ```

2. **Run Application**
   ```bash
   mvn spring-boot:run
   ```

## Architecture
- **Monolith**: Modular structure.
- **Database**: PostgreSQL with Flyway migrations.
- **Event Bus**: Apache Kafka.
- **Cache/Locking**: Redis.

## API Documentation
Once running, Swagger UI will be available at: `http://localhost:8080/swagger-ui.html` (To be configured)
