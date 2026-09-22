# CPEN 321 Term Project

## Development Info

**Deployed backend:**
- Public IP: `http://35.192.138.68:3000`
- Domain name: N/A

## Setup Steps 

1. **Clone the repository at the submitted commit:**
   ```bash
   git clone https://github.com/khaidoan2729/CPEN321-W2026
   cd CPEN321-W2026
   ```

2. **Set up the backend config:**
   ```bash
   cd backend
   cp .env.example .env
   ```
   Fill in `backend/.env` with exactly the values below (must match what the deployed instance uses, since access tokens are only valid against the secret that signed them):
   ```dotenv
   PORT=3000
   NODE_ENV=development
   MONGODB_URI=mongodb://localhost:27017/cpen321   # boilerplate, unused by current code
   GOOGLE_CLIENT_ID=579363237977-4hpuo0hq1um9ns877et9r0jshgo735jr.apps.googleusercontent.com
   JWT_SECRET=mlGUGpmjqQOzH/Z5QdoctJMVnZev2dy6OkBhAFVZgcjPzizB2UneQj0qE5JYYvIi7Rpcgkp5yd0hGIe/ACBUFA==
   ```

3. **Set up the frontend config:**
   ```bash
   cd ../frontend
   ```
   Create `local.properties` with:
   ```properties
   sdk.dir=/Users/macos/Library/Android/sdk 
   API_BASE_URL=http://35.192.138.68:3000
   GOOGLE_CLIENT_ID=579363237977-4hpuo0hq1um9ns877et9r0jshgo735jr.apps.googleusercontent.com
   ```
   > `sdk.dir` should point at **your own** Android SDK installation path, not the one shown above — this value is machine-specific. Android Studio will usually fill this in automatically on first open.

4. **Google Sign-In / debug key note:** Google Sign-In is registered against a specific debug-key SHA-1. If sign-in fails with a `DEVELOPER_ERROR` (error code 10) on your machine, it's because your locally auto-generated debug keystore has a different SHA-1 than the one registered on our Google Cloud project. Run:
   ```bash
   cd frontend
   ./gradlew signingReport
   ```
   and make sure SHA-1 under `Variant:debug` is `47:C6:A7:47:BC:9B:8D:62:CA:83:66:7A:57:E1:93:80:0F:19:D4:FE`.

5. **Deploy/run the backend** — it is already running on our Google Cloud VM at the public IP above, so no action is required to use the live instance. To run it yourselves instead:
   ```bash
   cd backend
   # ensure .env is filled in per step 2
   ./scripts/run-backend.sh
   ```

6. **Run the frontend** — either:
   - Open `frontend/` in Android Studio, select an emulator or physical device, and click **Run**, or
   - Use the provided script:
     ```bash
     AVD_NAME=<your AVD name> ./scripts/run-frontend.sh
     ```

## User Accounts / Login Credentials

N/A — no test account is required. Sign in with **your own Google account** via Button 1. The other two buttons don't apply here.
