# Deployment Guide: Koyeb + Neon 🚀

This guide explains how to deploy the **Payment Wallet** to a robust, free-tier friendly stack.

## Tech Stack
*   **Database:** [Neon](https://neon.tech) (Serverless PostgreSQL)
*   **Application:** [Koyeb](https://koyeb.com) (Docker Hosting)

---

## 1. Database Setup (Neon)
1.  Go to **[Neon.tech](https://neon.tech)** and sign up.
2.  Create a new **Project**.
3.  Copy the **Connection String** from the dashboard. It typically looks like:
    ```
    postgres://neondb_owner:*******@ep-shiny-cloud-123.aws.neon.tech/neondb?sslmode=require
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
    | `SPRING_DATASOURCE_URL` | *See detailed construction below* | **MUST FOLLOW FORMAT PRECISELY** |
    | `SPRING_DATASOURCE_USERNAME` | `<NEON_USER>` | From Neon Dashboard (e.g. `neondb_owner`) |
    | `SPRING_DATASOURCE_PASSWORD` | `<NEON_PASSWORD>` | From Neon Dashboard |
    | `SPRING_PROFILES_ACTIVE` | `prod` | |
    | `REDIS_ENABLED` | `false` | Defaults to false, but good to be explicit |
    | `KAFKA_ENABLED` | `false` | Defaults to false, but good to be explicit |

    ### ⚠️ How to construct `SPRING_DATASOURCE_URL` for Neon:
    The JDBC driver for Neon often requires the **Endpoint ID** to be passed explicitly if SNI fails.
    
    **Format:**
    ```
    jdbc:postgresql://<HOST>/<DB>?sslmode=require&options=endpoint%3D<ENDPOINT_ID>
    ```

    **Example:**
    If Neon gives you: `postgres://neondb_owner:pass@ep-shiny-cloud-123.aws.neon.tech/neondb?sslmode=require`

    *   **Host**: `ep-shiny-cloud-123.aws.neon.tech`
    *   **Database**: `neondb`
    *   **Endpoint ID**: `ep-shiny-cloud-123` (The first part of the host before `.aws.neon.tech`)

    **Your Connection String should be:**
    ```
    jdbc:postgresql://ep-shiny-cloud-123.aws.neon.tech/neondb?sslmode=require&options=endpoint%3Dep-shiny-cloud-123
    ```

    > **Note:** The `options=endpoint%3D...` part fixes the "Endpoint ID is not specified" error.

6.  **Expose Port:**
    *   Port: `8080`
    *   Protocol: `HTTP`

7.  Click **Deploy**.

---

## 3. Verification
Once deployed, click the **Public URL** provided by Koyeb.
*   **Health Check:** `/actuator/health` -> Should return `{"status":"UP"}`
*   **Swagger UI:** `/swagger-ui.html`

## Troubleshooting
*   **"Endpoint ID is not specified":** You missed the `&options=endpoint%3D<id>` part in the URL.
*   **"Kafka Connection Refused":** Ensure `KAFKA_ENABLED` is set to `false` (it is false by default now, but check your env vars).
