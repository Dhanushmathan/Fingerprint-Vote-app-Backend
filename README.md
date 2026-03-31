# 🗳️ VoteSecure — We Vote Fingerprint Voteing App

> **Java 17 · Spring Boot 3.2 · MySQL 8 · JWT · Docker**

Secure digital voting system backend with OTP verification, fingerprint biometric authentication, and strict one-vote-per-voter enforcement.

---

## 📁 Project Structure

```
backend/
├── src/main/java/com/voteapp/
│   ├── VoteSecureApplication.java       # Main entry point
│   ├── config/
│   │   └── SecurityConfig.java          # Spring Security + CORS
│   ├── controller/
        └── PartyController.java         # Partys register
        └── VoterController.java         # Voters register
        └── VoteController.java          # One vote & Verify voters
        └── OtpController.java           # OTP authentication
│   ├── service/
│   │   ├── PartyService.java            # Party + candidate logic
│   │   ├── VoterService.java            # Voter registration
│   │   ├── OtpService.java              # OTP generate + verify
│   │   ├── VoteService.java             # Vote cast + one-vote enforcement
│   │   ├── JwtService.java              # JWT single-use token
│   │   └── FingerprintService.java      # SHA-256 biometric hash
│   ├── model/
│   │   ├── Party.java                   # Party entity
│   │   ├── Candidate.java               # Candidate entity
│   │   ├── Voter.java                   # Voter entity
│   │   ├── Vote.java                    # Vote record (unique per voter)
│   │   └── OtpSession.java              # OTP session (10 min expiry)
│   ├── repository/
│   │   └── Repositories.java            # All JPA repositories
│   ├── dto/
│   │   └── DTOs.java                    # Request + Response objects
│   └── exception/
│       ├── Exceptions.java              # AlreadyVotedException etc.
│       └── GlobalExceptionHandler.java  # @RestControllerAdvice
└── src/main/resources/
    ├── application.properties           # Config + env vars
    └── fix_data.sql                     # DB reset script
```

---

## ⚙️ Prerequisites

| Tool    | Version  | Download |
|---------|----------|----------|
| Java    | 17+      | https://adoptium.net |
| Maven   | 3.8+     | https://maven.apache.org |
| MySQL   | 8.0+     | https://dev.mysql.com |
| Docker  | 24+      | https://docker.com (optional) |

---

✅ Backend starts at: **http://localhost:8080**

> Spring Boot auto-creates all tables on first run (`ddl-auto=update`)

---

## 🔌 API Endpoints

### Parties
| Method | Endpoint           | Description              |
|--------|--------------------|--------------------------|
| POST   | `/api/parties`     | Register new party       |
| GET    | `/api/parties`     | Get all parties          |
| GET    | `/api/parties/{id}`| Get party by ID          |
| DELETE | `/api/parties/{id}`| Delete party             |

### Voters
| Method | Endpoint                  | Description           |
|--------|---------------------------|-----------------------|
| POST   | `/api/voters/register`    | Register voter + FP   |
| GET    | `/api/voters/{voterId}`   | Get voter by ID       |
| GET    | `/api/voters`             | Get all voters        |

### OTP
| Method | Endpoint          | Description                   |
|--------|-------------------|-------------------------------|
| POST   | `/api/otp/send`   | Send OTP to mobile            |
| POST   | `/api/otp/verify` | Verify OTP → returns JWT token|

### Votes
| Method | Endpoint              | Description                |
|--------|-----------------------|----------------------------|
| POST   | `/api/votes/cast`     | Cast vote (FP + OTP required)|
| GET    | `/api/votes/results`  | Live election results      |

---

## 📋 Sample API Requests

### Register a Party
```bash
curl -X POST http://localhost:8080/api/parties \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Progressive Alliance",
    "leaderName": "Arjun Kumar",
    "symbol": "⚡",
    "colorTheme": "sw1",
    "manifesto": "Infrastructure, Education",
    "foundedYear": 2018,
    "candidates": [
      {
        "name": "Arjun Kumar",
        "age": 42,
        "qualifications": "B.Tech, MBA",
        "ward": "Ward 7"
      }
    ]
  }'
```

### Register a Voter
```bash
curl -X POST http://localhost:8080/api/voters/register \
  -H "Content-Type: application/json" \
  -d '{
    "voterId": "VOT-001",
    "fullName": "Ravi Kumar",
    "mobileNumber": "+919876543210",
    "ward": "Ward 7",
    "fingerprintToken": "FP_ENROLLED_VOT-001"
  }'
```

### Send OTP
```bash
curl -X POST http://localhost:8080/api/otp/send \
  -H "Content-Type: application/json" \
  -d '{
    "voterId": "VOT-001",
    "mobileNumber": "+919876543210"
  }'
```
> 💡 Dev mode: OTP printed in console logs. Check terminal!

### Verify OTP → Get Session Token
```bash
curl -X POST http://localhost:8080/api/otp/verify \
  -H "Content-Type: application/json" \
  -d '{
    "voterId": "VOT-001",
    "mobileNumber": "+919876543210",
    "otpCode": "123456"
  }'
```
Response:
```json
{
  "success": true,
  "message": "OTP verified successfully.",
  "sessionToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Cast Vote
```bash
curl -X POST http://localhost:8080/api/votes/cast \
  -H "Content-Type: application/json" \
  -d '{
    "voterId": "VOT-001",
    "candidateId": 1,
    "fingerprintToken": "FP_ENROLLED_VOT-001",
    "otpSessionToken": "eyJhbGciOiJIUzI1NiJ9..."
  }'
```

### Already Voted — Error Response (HTTP 409)
```json
{
  "error": "ALREADY_VOTED",
  "message": "You have already cast your vote in this election!",
  "voterId": "VOT-001",
  "votedForCandidate": "Arjun Kumar",
  "votedForParty": "Progressive Alliance",
  "votedAt": "2025-03-22T14:32:07"
}
```

---

## 🔐 Security Features

| Feature               | How it works                                         |
|-----------------------|------------------------------------------------------|
| One-vote enforcement  | `hasVoted` flag in DB + unique constraint on `votes` table |
| OTP verification      | 6-digit code, 10-min expiry, single-use              |
| Session token         | JWT (15 min) issued after OTP verify                 |
| Token blacklist       | Used tokens stored in memory — replay attacks blocked |
| Fingerprint auth      | SHA-256 hash stored at register, verified at vote    |
| Duplicate detection   | `blockedAttempts` counter per voter                  |
| CORS protection       | `setAllowedOriginPatterns` with trim                 |
| Input validation      | Jakarta Bean Validation on all DTOs                  |

---

## 🐳 Docker

### Build Image
```bash
docker build -t votesecure-backend:v1 .
```

### Run with Docker
```bash
docker run -p 8080:8080 \
  -e MYSQL_URL=jdbc:mysql://host.docker.internal:3306/votedb?useSSL=false \
  -e MYSQL_USER=root \
  -e MYSQL_PASSWORD=yourpassword \
  -e JWT_SECRET=YourSuperSecretKey256BitsLong! \
  -e CORS_ORIGINS=http://localhost:3000 \
  votesecure-backend:latest
```

### Push to Docker Hub
```bash
docker build -t yourusername/votesecure-backend:v1 .
docker push yourusername/votesecure-backend:v1
```

---

## ☁️ Deploy on Render.com (Free)

### Step 1 — Push to GitHub
```bash
git add .
git commit -m "VoteSecure backend"
git push origin main
```

### Step 2 — Render Setup
1. Go to **https://render.com** → New → **Web Service**
2. Connect your GitHub repo
3. Set **Root Directory** = `backend`
4. **Runtime** = `Docker`
5. Add Environment Variables:

```
MYSQL_URL       = jdbc:mysql://HOST:PORT/DBNAME?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
MYSQL_USER      = your_db_user
MYSQL_PASSWORD  = your_db_password
JWT_SECRET      = YourSuperSecretKey256BitsLong!
CORS_ORIGINS    = https://fingerprint-vote-app-frentend.vercel.app
PORT            = 8080
```

> ⚠️ `CORS_ORIGINS` — no spaces after comma!
> ```
> ✅ https://app.vercel.app,http://localhost:5173
> ❌ https://app.vercel.app, http://localhost:5173
> ```

### Step 3 — Deploy
Click **Deploy** → Wait 3-5 min → Backend live! ✅

Backend URL: `https://fingerprint-vote-app-backend.onrender.com`

---

## 🗄️ Database Reset (if corrupt data)

```sql
-- Run fix_data.sql in MySQL Workbench
-- Located at: src/main/resources/fix_data.sql

UPDATE candidates SET vote_count = 0;
DELETE FROM votes;
UPDATE voters SET has_voted = 0,
  voted_for_candidate_id = NULL,
  voted_at = NULL,
  blocked_attempts = 0;
```

Then restart the backend server.

---

## 🌿 Environment Variables Reference

| Variable          | Required | Default                    | Description           |
|-------------------|----------|----------------------------|-----------------------|
| `MYSQL_URL`       | ✅ Yes   | localhost:3306/votedb      | Full JDBC URL         |
| `MYSQL_USER`      | ✅ Yes   | root                       | DB username           |
| `MYSQL_PASSWORD`  | ✅ Yes   | rootmysql                       | DB password           |
| `JWT_SECRET`      | ✅ Yes   | (default provided)         | 256-bit signing key   |
| `CORS_ORIGINS`    | ✅ Yes   | localhost:5173             | Comma-separated URLs  |
| `PORT`            | ⬜ No    | 8080                       | Server port           |

---

## 🛠️ Common Errors & Fixes

### CORS Error
```
Access-Control-Allow-Origin header missing
```
**Fix:** Add your Vercel URL to `CORS_ORIGINS` env var — no spaces after comma.

### DB Connection Failed
```
Communications link failure
```
**Fix:** Check `MYSQL_URL` starts with `jdbc:mysql://` not `mysql://`

### Fingerprint Mismatch
```
Fingerprint mismatch! Biometric verification failed.
```
**Fix:** Token format must match exactly:
- Register: `FP_ENROLLED_VOT001`
- Vote: `FP_ENROLLED_VOT001` (same!)

### 401 Unauthorized
```
Full authentication is required
```
**Fix:** Check `application.properties` — no underscores in property keys!
```properties
# Wrong ❌
__app.jwt.secret__=...

# Correct ✅
app.jwt.secret=...
```

---

*VoteSecure Backend — Built with Java 17 + Spring Boot 3.2* 💜