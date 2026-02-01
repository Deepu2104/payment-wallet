# Deployment Guide: Frontend (Vercel) + Backend (Koyeb) 🚀

## Architecture
*   **Backend:** Koyeb (hosts Spring Boot API + Swagger)
*   **Database:** Neon (PostgreSQL)
*   **Frontend:** Vercel (hosts React App)

---

## 1. Backend Setup (Koyeb)
*(You have already done this)*
*   **Koyeb URL:** e.g., `https://ethnic-lizbeth-payment-wallet-b8b58d61.koyeb.app`
*   **Important:** This URL is for your API (Swagger), not the React app.

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
