# 🛡️ CampusLink Backend - Microservices Architecture

<div align="center">

[![Turkish](https://img.shields.io/badge/lang-TR-red)](README.tr.md)
[![English](https://img.shields.io/badge/lang-EN-blue)](README.md)

**Scalable social platform backend built with Spring Boot, Kafka, and Redis.**

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Driven-black)
![Redis](https://img.shields.io/badge/Redis-Caching-red)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)

</div>

## 🚀 Key Features

- **Microservices:** Orchestrated via API Gateway and Eureka Discovery.
- **Event-Driven:** Uses **Kafka** for asynchronous invitation processing and notifications.
- **Security:** Stateless **JWT** authentication with Role-Based Access Control (RBAC).
- **Performance:** **Redis** caching implementation for high-traffic endpoints.
- **Media:** Cloudinary integration for profile and event image storage.

## 🏗 Architecture

| Service | Port | Description |
| :--- | :--- | :--- |
| **API Gateway** | 8080 | Entry point, CORS config, Routing |
| **User Service** | Random | Auth, Registration, Profile Management |
| **Club Service** | Random | Club creation, Membership logic |
| **Event Service** | Random | Event scheduling, Participants |
| **Invitation Service** | Random | Invite system logic (User-to-User) |

## 🛠 Tech Stack

*   **Framework:** Spring Boot 4.1, Spring Cloud
*   **Database:** PostgreSQL
*   **Messaging:** Apache Kafka
*   **Cache:** Redis
*   **Build Tool:** Maven
*   **Containerization:** Docker & Docker Compose

## 🏃‍♂️ How to Run

1.  **Start Infrastructure (Kafka, Redis):**
    ```bash
    docker-compose up -d
    ```
2.  **Run Services:**
    Start `DiscoveryService` first, then `ApiGateway`, followed by other services.

---
*Developed by Yunus BAYDAR*