# 🛡️ CampusLink Backend - Mikroservis Mimarisi

<div align="center">

[![Turkish](https://img.shields.io/badge/lang-TR-red)](README.tr.md)
[![English](https://img.shields.io/badge/lang-EN-blue)](README.md)

**Spring Boot, Kafka ve Redis ile geliştirilmiş ölçeklenebilir sosyal platform altyapısı.**

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Olay_Güdümlü-black)
![Redis](https://img.shields.io/badge/Redis-Önbellek-red)
![MySQL](https://img.shields.io/badge/MySQL-Veritabanı-blue)

</div>

## 🚀 Temel Özellikler

- **Mikroservisler:** API Gateway ve Eureka Discovery ile yönetilen yapı.
- **Olay Güdümlü (Event-Driven):** Davetiye ve bildirim işlemleri için **Kafka** entegrasyonu.
- **Güvenlik:** **JWT** ile stateless kimlik doğrulama ve Rol Bazlı Erişim (RBAC).
- **Performans:** Yoğun istek alan listelemeler için **Redis** önbellekleme.
- **Medya:** Cloudinary ile görsel yönetimi.

## 🏗 Mimari

| Servis | Port | Açıklama |
| :--- | :--- | :--- |
| **API Gateway** | 8080 | Tek giriş noktası, CORS, Yönlendirme |
| **User Service** | Random | Kayıt, Giriş, Profil Yönetimi |
| **Club Service** | Random | Kulüp kurma, Üye işlemleri |
| **Event Service** | Random | Etkinlik takvimi, Katılımcılar |
| **Invitation Service** | Random | Davetiye mantığı |

## 🛠 Teknolojiler

*   **Framework:** Spring Boot 3, Spring Cloud
*   **Veritabanı:** PostgreSQL
*   **Mesajlaşma:** Apache Kafka
*   **Cache:** Redis
*   **Derleme:** Maven
*   **Konteyner:** Docker & Docker Compose

## 🏃‍♂️ Kurulum

1.  **Altyapıyı Başlatın (Kafka, Redis):**
    ```bash
    docker-compose up -d
    ```
2.  **Servisleri Çalıştırın:**
    Önce `DiscoveryService`, sonra `ApiGateway` ve ardından diğer servisleri başlatın.

---
*Geliştirici: Yunus BAYDAR*