# Deployment Guide: Koyeb + Neon 🚀

This guide explains how to deploy the **Payment Wallet** to a robust, free-tier friendly stack.

## Tech Stack
*   **Database:** [Neon](https://neon.tech) (Serverless PostgreSQL)
*   **Application:** [Koyeb](https://koyeb.com) (Docker Hosting)

---

## 1. Database Setup (Neon)
1.  Go to **[Neon.tech](https://neon.tech)** and sign up.
2.  Create a new **Project**.
3.  Copy the **Connection String** from the dashboard. It will look like:
    ```
    postgres://neondb_owner:*******@ep-shiny-....aws.neon.tech/paywallet?sslmode=require
    ```
---

## 2. Application Setup (Koyeb)
1.  Go to **[Koyeb.com](https://koyeb.com)** and sign up.
2.  Click **Create App**.
3.  Select **GitHub** as the source and choose your repository: `Deepu2104/payment-wallet`.
4.  **Builder Configuration:**
    *   **Builder:** Dockerfile
    *   **Docker Location:** `Dockerfile` (default)
    *   **Privileged:** Unchecked

5.  **Environment Variables (CRITICAL):**
    Click on "Environment Variables" and add these **exact** values.

    | Key | Value | Note |
    | :--- | :--- | :--- |
    | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<NEON_HOST>/<DB_NAME>?sslmode=require` | **See instructions below** |
    | `SPRING_DATASOURCE_USERNAME` | `<NEON_USER>` | From Neon Dashboard |
    | `SPRING_DATASOURCE_PASSWORD` | `<NEON_PASSWORD>` | From Neon Dashboard |
    | `SPRING_PROFILES_ACTIVE` | `prod` | |
    | `REDIS_ENABLED` | `false` | Disables Redis (uses local memory) |
    | `KAFKA_ENABLED` | `false` | Disables Kafka (uses local memory) |

    ### ⚠️ How to construct `SPRING_DATASOURCE_URL`:
    Neon gives you: `postgres://user:pass@ep-shiny-cloud.aws.neon.tech/neondb`
    
    You must convert it to Java format:
    `jdbc:postgresql://ep-shiny-cloud.aws.neon.tech/neondb?sslmode=require`

    *   **Host:** `ep-shiny-cloud.aws.neon.tech` (The part after `@`)
    *   **Database:** `neondb` (The part after `/`)
    *   **Suffix:** `?sslmode=require` (Required for Neon)

6.  **Expose Port:**
    *   Port: `8080`
    *   Protocol: `HTTP`

7.  Click **Deploy**.

---

## 3. Verification
Once deployed, click the **Public URL** provided by Koyeb (e.g., `https://payment-wallet-xyz.koyeb.app`).
*   The Swagger UI should be available at: `/swagger-ui.html`
*   The Health Check: `/actuator/health`

## Troubleshooting
*   **Database Connection Failed:** Double-check the `SPRING_DATASOURCE_URL` format. It MUST start with `jdbc:postgresql://` and end with `?sslmode=require`.
*   **Build Failures:** Check the "Build Logs" tab in Koyeb for Maven errors.
