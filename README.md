#  Stock Portfolio Monitoring App — Microservices Architecture

A scalable, distributed **Stock Portfolio Monitoring System** built using **Spring Boot Microservices**. This application allows users to manage portfolios, track real-time stock prices, receive alerts, and generate reports.

---

## Tech Stack

**Backend**

* Spring Boot 3.x
* Spring Cloud (Eureka, Config, Gateway)
* Spring Security + OAuth2 + JWT

**Architecture**

* Microservices (Database per Service)
* API Gateway Pattern
* Event-Driven Architecture

**Communication**

* REST (OpenFeign)
* RabbitMQ (Async Messaging)

**Data & Caching**

* MySQL (per-service schema)
* Redis (cache + token blacklist + rate limiting)

**Documentation**

* Swagger / OpenAPI 3

---

## Architecture Overview

* API Gateway → Entry point, routing, JWT validation
* Eureka Server → Service discovery
* Config Server → Centralized configuration (Git-backed)
* RabbitMQ → Event-driven communication
* Redis → Caching and token management

Each microservice is independently deployable and owns its own database.

---

## Project Structure

```
stock-portfolio-microservices/
├── config-server/
├── eureka-server/
├── api-gateway/
├── auth-service/
├── user-service/
├── portfolio-service/
├── price-fetcher-service/
├── alert-service/
├── notification-service/
└── report-service/
```

---

## Infrastructure Services

* **Config Server (Port 8888)** → Centralized configuration using Git
* **Eureka Server (Port 8761)** → Service registry
* **API Gateway (Port 8080)** → Routing + JWT validation

---

## Core Features

* User authentication (JWT + OAuth2)
* Portfolio management & gain/loss calculation
* Real-time stock price fetching
* Price alerts & notifications
* Report generation (PDF / Excel)
* Event-driven processing with RabbitMQ
* Redis caching for performance

---

## Development Workflow

* `main` → Stable code
* `dev` → Integration branch
* `feature/*` → Feature development

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/stock-portfolio-microservices.git
cd stock-portfolio-microservices
```

---

### 2. Start Infrastructure Services (ORDER MATTERS)

```bash

# Start Config Server

cd config-server
mvn spring-boot:run

# Start Eureka Server

cd ../eureka-server
mvn spring-boot:run

# Start API Gateway

cd ../api-gateway
mvn spring-boot:run
```

---

### 3. Verify Services

* Eureka Dashboard → http://localhost:8761
* Gateway → http://localhost:8080

---

## Security

* JWT-based authentication
* OAuth2 login support (Google, GitHub)
* Token validation at API Gateway
* Redis-based token blacklist

---

## Inter-Service Communication

* **Feign Clients** → synchronous calls
* **RabbitMQ Events**:

    * UserRegistered
    * PriceUpdated
    * AlertTriggered
    * DailySummaryReady

---

## Design Principles

* Single Responsibility per service
* Database per service
* API-first design
* Async-first communication
* Loose coupling via events

---

## Notes

* No cross-service DB queries (strict microservices rule)
* All services communicate via REST or messaging
* Configurations managed via Config Server

---

## Future Enhancements

* WebSocket live price updates
* AI-based buy/sell recommendations
* Distributed tracing (Zipkin)
* Docker & Kubernetes deployment
* Circuit breaker (Resilience4j)

---

## License

This project is for educational and demonstration purposes.
