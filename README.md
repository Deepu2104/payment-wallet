# 💸 PayWallet

A production-grade digital wallet system built with modern technologies. It provides a secure and scalable platform for users to manage funds, transfer money, and track transactions, backed by a robust fraud detection system.

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green.svg)
![React](https://img.shields.io/badge/React-19-blue.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green)

---

## 🚀 Features

- **User Accounts**: Secure registration and login with JWT authentication.
- **Wallet Management**: Add money to wallet, view balance.
- **Money Transfer**: Safe and fast peer-to-peer money transfers.
- **Fraud Detection**: Asynchronous fraud detection using **Apache Kafka** to analyze transactions.
- **Security**: Robust security with Spring Security.
- **Scalability**: Designed with **Redis** for caching and rate limiting.
- **Modern UI**: Responsive and sleek frontend built with **React, TypeScript, and Tailwind CSS**.

## 🛠 Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: PostgreSQL (with Flyway for migrations)
- **Messaging**: Apache Kafka
- **Caching**: Redis
- **Documentation**: Swagger / OpenAPI

### Frontend
- **Framework**: React 19 (Vite)
- **Language**: TypeScript
- **Styling**: Tailwind CSS 4
- **Icons**: Lucide React

### Infrastructure / DevOps
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit 5, Testcontainers

---

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java Development Kit (JDK) 17**
- **Node.js** (v20+ recommended)
- **Docker** and **Docker Compose**
- **Maven** (optional if using `mvnw` wrapper)

---

## ⚡ Quick Start (Local Development)

### 1. Start Infrastructure
Start PostgreSQL, Redis, and Kafka services using Docker Compose:

```bash
docker-compose up -d
```

### 2. Backend Setup
Navigate to the root directory and run the Spring Boot application:

```bash
mvn spring-boot:run
```
*The backend will start on `http://localhost:8080`.*

### 3. Frontend Setup
Open a new terminal, navigate to the `frontend` directory, and start the development server:

```bash
cd frontend
npm install
npm run dev
```
*The frontend will start on `http://localhost:5173`.*

---

## ⚙️ Configuration

The application uses `application.yml` for configuration. You can override settings using environment variables.

| Variable | Description | Default |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | JDBC URL for PostgreSQL | `jdbc:postgresql://localhost:5432/paywallet` |
| `REDIS_ENABLED` | Enable specialized Redis features | `false` (uses in-memory fallback if false) |
| `KAFKA_ENABLED` | Enable Kafka messaging | `false` (uses synchronous fallback if false) |
| `REDIS_HOST` | Redis Host | `localhost` |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka Brokers | `localhost:9092` |

See [DEPLOYMENT.md](./DEPLOYMENT.md) for advanced configuration and cloud deployment instructions.

---

## 📚 API Documentation

Once the backend is running, you can access the interactive API documentation (Swagger UI) at:

👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 🧪 Running Tests

Run unit and integration tests using Maven. Note that integration tests use **Testcontainers** and require Docker to be running.

```bash
mvn test
```

---

## 📦 Deployment

For detailed deployment instructions on **Koyeb (Backend)** and **Vercel (Frontend)**, please refer to the [Deployment Guide](./DEPLOYMENT.md).

---
