# Deployment Guide: Frontend (Vercel) + Backend (Koyeb) 🚀

## Architecture
*   **Backend:** Koyeb (hosts Spring Boot API + Swagger)
*   **Database:** Neon (PostgreSQL)
*   **Frontend:** Vercel (hosts React App)

---

## 1. Backend Setup (Koyeb)
*   **Koyeb URL:** e.g., `https://ethnic-lizbeth-payment-wallet-b8b58d61.koyeb.app`
*   **Important:** This URL is for the API (Swagger), not the React app.

---

## 2. Frontend Setup (Vercel)
The frontend must be deployed separately to Vercel.

1.  **Push Code:** Ensure your `frontend` folder is committed to GitHub.
2.  Go to **[Vercel.com](https://vercel.com)** and sign up/login.
3.  Click **Add New...** -> **Project**.
4.  Import your `payment-wallet` repository.
5.  **Configure Project:**
    *   **Framework Preset:** Vite
    *   **Root Directory:** Click "Edit" and select `frontend`. **(CRITICAL STEP)**
    *   **Build Command:** `npm run build` (default)
    *   **Output Directory:** `dist` (default)

6.  **Environment Variables:**
    Expand the "Environment Variables" section.
    *   **Key:** `VITE_API_URL`
    *   **Value:** `https://<YOUR_KOYEB_APP_NAME>.koyeb.app/api/v1`
    *   **Important:** You **MUST** append `/api/v1` to the end of the URL.
    *   **Action:** Click "Add".

7.  Click **Deploy**.

---

## 3. Verify
Once Vercel finishes:
1.  Open the **Vercel domain** (e.g., `https://payment-wallet-frontend.vercel.app`).
2.  Try to **Login/Register**.
3.  It should communicate with your Koyeb backend.

## Troubleshooting
*   **CORS Error:** I have already updated the backend to allow connections from Vercel. Redeploy the backend if you haven't recently.
*   **404 on API calls:** Ensure `VITE_API_URL` is set correctly in Vercel settings.

---

## 4. Future Scaling: Redis & Kafka 📈

Currently, the app runs in **"Free Stack Mode"**:
*   **Redis** is DISABLED (Tokens stored in RAM).
*   **Kafka** is DISABLED (Events processed synchronously).

To enable them heavily increases performance and reliability.

### Step 1: Get Resources (Free Tier Recommended)
*   **Redis:** [Upstash Redis](https://upstash.com) (Excellent serverless Redis).
*   **Kafka:** [Upstash Kafka](https://upstash.com) or [Confluent Cloud](https://confluent.io).

### Step 2: Update Koyeb Environment Variables
Add these variables to your running Koyeb service to automatically enable the features.

| Key | Value Template |
| :--- | :--- |
| **`REDIS_ENABLED`** | `true` |
| **`REDIS_HOST`** | `your-upstash-redis.com` |
| **`REDIS_PORT`** | `6379` |
| **`REDIS_PASSWORD`** | `your_redis_password` |
| | |
| **`KAFKA_ENABLED`** | `true` |
| **`KAFKA_BOOTSTRAP_SERVERS`** | `your-kafka-broker:9092` |
| **`KAFKA_SECURITY_PROTOCOL`** | `SASL_SSL` (usually for cloud) |
| **`KAFKA_SASL_MECHANISM`** | `SCRAM-SHA-256` (check provider) |
| **`KAFKA_SASL_JAAS_CONFIG`** | `org.apache.kafka.common.security.scram.ScramLoginModule required username="<USER>" password="<PASS>";` |

**No code changes are needed!** The app detects `ENABLED=true` and switches from RAM to the real services automatically.
