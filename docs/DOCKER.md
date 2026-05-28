# Docker Setup

이 프로젝트의 Docker 구성은 두 가지 용도로 나뉩니다.

- 기본 실행: PostgreSQL만 Docker로 실행하고, 백엔드/프론트엔드는 IDE에서 실행
- 전체 실행: PostgreSQL, Spring Boot 백엔드, Vue 프론트엔드를 모두 Docker로 실행

## 사전 준비

Docker Desktop을 실행한 뒤 엔진이 준비됐는지 확인합니다.

```powershell
docker --version
docker compose version
docker info
```

`docker info`가 실패하면 Docker Desktop 초기 설정, WSL2 설정, 재부팅 여부를 먼저 확인해야 합니다.

## DB만 실행

로컬 개발에서는 이 방식을 권장합니다.

```powershell
docker compose up -d
```

PostgreSQL 접속 정보:

```text
host: localhost
port: 5432
database: stravamate_passport
username: stravamate
password: stravamate
```

상태 확인:

```powershell
docker compose ps
docker compose logs postgres
```

중지:

```powershell
docker compose down
```

DB 데이터까지 삭제:

```powershell
docker compose down -v
```

## 전체 앱 실행

먼저 Docker용 환경변수 파일을 만듭니다.

```powershell
copy .env.docker.example .env.docker
```

`.env.docker`에서 Strava 값을 설정합니다.

```text
STRAVA_CLIENT_ID=...
STRAVA_CLIENT_SECRET=...
STRAVA_REDIRECT_URI=http://localhost:8080/api/auth/strava/callback
```

전체 스택 실행:

```powershell
docker compose --env-file .env.docker --profile app up --build
```

접속 주소:

```text
Frontend: http://localhost:5173
Backend:  http://localhost:8080
Health:   http://localhost:8080/api/health
```

백그라운드 실행:

```powershell
docker compose --env-file .env.docker --profile app up --build -d
```

중지:

```powershell
docker compose --profile app down
```

## Demo Mode로 프론트만 확인

Strava API 없이 화면만 빠르게 확인하려면 `.env.docker`에서 아래처럼 설정하고 다시 빌드합니다.

```text
VITE_DEMO_MODE=true
```

```powershell
docker compose --env-file .env.docker --profile app up --build frontend
```

## 주의사항

- Strava access token, refresh token, client secret은 프론트엔드 컨테이너에 넣지 않습니다.
- `VITE_*` 값은 Vue 빌드 시점에 정적 파일에 포함됩니다. 민감한 값은 절대 `VITE_*`로 만들지 않습니다.
- OAuth callback URL은 Strava 앱 설정과 `STRAVA_REDIRECT_URI`가 정확히 같아야 합니다.
- 백엔드 컨테이너 내부에서는 DB host가 `localhost`가 아니라 Compose 서비스 이름인 `postgres`입니다.
