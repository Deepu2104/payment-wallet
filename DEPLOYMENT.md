## 1. One-Click Deployment (Recommended)
This project includes a `render.yaml` Blueprint.

1. Go to [Render Dashboard](https://dashboard.render.com).
2. Click **New +** -> **Blueprint**.
3. Connect this repository.
4. Render will automatically detect the `render.yaml` and propose creating:
   - `paywallet-db` (PostgreSQL)
   - `paywallet-service` (Web Service)
5. Click **Apply**. 

That's it! Render will build the Docker image (Frontend + Backend) and deploy it.

## 2. Configuration Notes
By default, we run in "Demo Mode" to avoid needing external Redis/Kafka services (which cost money).
- `REDIS_ENABLED`: `false` (Uses in-memory fallback)
- `KAFKA_ENABLED`: `false` (Uses direct method calls)

To enable these features later, simply spin up Redis/Kafka services and set these variables to `true`.
