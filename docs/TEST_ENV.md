# Test Environment

실제 Strava 계정으로 동작을 확인하려면 프론트엔드 demo mode가 아니라 백엔드, DB, Strava OAuth 설정이 필요합니다.

## 설치 필요 항목

필수:

- JDK 17
- Gradle 또는 Gradle Wrapper
- Node.js 20 이상 권장
- Docker Desktop 또는 로컬 PostgreSQL

Docker는 필수는 아니지만 PostgreSQL을 가장 쉽게 띄우는 방법입니다.

## 설치 확인

PowerShell:

```powershell
java -version
.\gradlew.bat --version
node -v
npm -v
docker --version
```

현재 프로젝트는 `docker-compose.yml`로 PostgreSQL만 실행합니다.

## Docker로 PostgreSQL 실행

```powershell
docker compose up -d
```

DB 정보:

```text
host: localhost
port: 5432
database: stravamate_passport
username: stravamate
password: stravamate
```

종료:

```powershell
docker compose down
```

데이터까지 삭제:

```powershell
docker compose down -v
```

## Docker 없이 PostgreSQL을 쓰는 경우

로컬 PostgreSQL에 아래 DB와 사용자를 직접 만듭니다.

```sql
CREATE DATABASE stravamate_passport;
CREATE USER stravamate WITH PASSWORD 'stravamate';
GRANT ALL PRIVILEGES ON DATABASE stravamate_passport TO stravamate;
```

그 다음 `.env.backend.example` 값을 로컬 DB 정보에 맞게 바꿔서 사용합니다.

## Strava API 설정

Strava API Application에서 callback URL을 백엔드와 맞춥니다.

```text
http://localhost:8080/api/auth/strava/callback
```

환경변수:

```text
STRAVA_CLIENT_ID=...
STRAVA_CLIENT_SECRET=...
STRAVA_REDIRECT_URI=http://localhost:8080/api/auth/strava/callback
FRONTEND_BASE_URL=http://localhost:5173
```

## 백엔드 실행

PowerShell에서 환경변수를 설정한 뒤 실행합니다.

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/stravamate_passport"
$env:DB_USERNAME="stravamate"
$env:DB_PASSWORD="stravamate"
$env:STRAVA_CLIENT_ID="your-strava-client-id"
$env:STRAVA_CLIENT_SECRET="your-strava-client-secret"
$env:STRAVA_REDIRECT_URI="http://localhost:8080/api/auth/strava/callback"
$env:FRONTEND_BASE_URL="http://localhost:5173"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173,http://127.0.0.1:5173"
$env:NOMINATIM_USER_AGENT="StravaMatePassport/0.1 local"

.\gradlew.bat bootRun
```

Health check:

```powershell
curl http://localhost:8080/api/health
```

## 프론트엔드 실행

`frontend/.env`:

```text
VITE_API_BASE_URL=http://localhost:8080
VITE_DEMO_MODE=false
```

실행:

```powershell
cd frontend
npm install
npm run dev
```

접속:

```text
http://127.0.0.1:5173/login
```

## 실제 계정 테스트 순서

1. Docker Desktop 실행
2. `docker compose up -d`
3. 백엔드 환경변수 설정
4. `.\gradlew.bat bootRun`
5. 프론트 `.env`에서 `VITE_DEMO_MODE=false`
6. `cd frontend`
7. `npm run dev`
8. 브라우저에서 `/login` 접속
9. Strava OAuth 승인
10. `/sync` 또는 Passport 화면에서 활동 동기화
11. `/passport`에서 지도/국가/도시 카드 확인

## 자주 막히는 지점

- `java` 명령이 없으면 JDK 17 설치가 필요합니다.
- `gradle` 명령이 없으면 Gradle 설치 또는 Gradle Wrapper 추가가 필요합니다.
- Docker가 없으면 PostgreSQL을 직접 설치해야 합니다.
- Strava callback URL이 다르면 OAuth callback이 실패합니다.
- 공개 Nominatim은 대량 요청에 적합하지 않습니다. 로컬 테스트 수준으로만 사용하세요.
