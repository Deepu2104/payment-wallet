# Simplified Deployment: One-Click to Render

This guide gets your whole application live in minutes by bundling everything into a single service. No external setup for Redis or Kafka is required.

## 1. Database Setup (Render)
1. Go to [Render](https://render.com/) and create a **New** -> **PostgreSQL**.
2. Name it `paywallet-db`. 
3. After creation, copy the **Internal Database URL**.

## 2. Web Service Setup (Render)
1. Click **New** -> **Web Service**.
2. Connect your GitHub repository.
3. Select **Docker** as the Runtime.
4. Add the following **Environment Variables**:
   - `SPRING_DATASOURCE_URL`: (Paste your Render Internal Database URL)
   - `REDIS_ENABLED`: `false` (Enables built-in demo mode)
   - `KAFKA_ENABLED`: `false` (Enables built-in demo mode)
   - `JWT_SECRET_KEY`: (A random 64-character hex string)
5. Click **Deploy**.

**That's it!** Your app will be live at the Render URL provided (e.g., `https://paywallet.onrender.com`).
