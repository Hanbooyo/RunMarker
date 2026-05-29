# Hosting Guide

주변 사람에게 데모를 공유하는 1차 배포 기준입니다.

## 추천 구성

- Frontend: Cloudflare Pages
- Backend: Render Web Service
- Database: Neon Postgres

이 구성은 무료 구간으로 시작하기 쉽습니다. 단, 무료 백엔드는 잠자기, 재시작, 사용량 제한이 있을 수 있으므로 장기 운영용은 아닙니다.

## 브랜치 전략

- `local-dev`: 로컬 개발용
- `hosting-prod`: 외부 배포용
- `main`: 안정 기준 브랜치

배포 관련 설정은 `hosting-prod`에서 먼저 적용하고, 안정화 후 필요하면 `main`에 병합합니다.

## Strava 설정

Strava App 설정에서 callback domain은 API 서버 도메인만 입력합니다.

예:

```text
api.your-domain.example
```

백엔드 환경변수에는 전체 callback URL을 입력합니다.

```text
STRAVA_REDIRECT_URI=https://api.your-domain.example/api/auth/strava/callback
```

## Backend 환경변수

`.env.backend.production.example`을 기준으로 Render 환경변수에 등록합니다.

필수:

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://HOST:5432/DATABASE?sslmode=require
DB_USERNAME=...
DB_PASSWORD=...
FRONTEND_BASE_URL=https://your-frontend-domain.example
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.example
STRAVA_CLIENT_ID=...
STRAVA_CLIENT_SECRET=...
STRAVA_REDIRECT_URI=https://your-api-domain.example/api/auth/strava/callback
TOKEN_CIPHER=aes-gcm
TOKEN_ENCRYPTION_KEY=...
DEV_AUTH_ENABLED=false
DEBUG_USER_HEADER_ENABLED=false
```

`TOKEN_ENCRYPTION_KEY`는 32바이트 AES 키를 Base64로 인코딩한 값입니다.

생성 예:

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

## Render Backend 예시

- Build Command:

```text
./gradlew clean bootJar
```

- Start Command:

```text
java -jar build/libs/stravamate-passport-0.0.1-SNAPSHOT.jar
```

Windows 로컬이 아니라 Render Linux 환경에서 실행되므로 `gradlew`를 사용합니다.

## Cloudflare Pages Frontend 예시

- Root directory:

```text
frontend
```

- Build command:

```text
npm ci && npm run build
```

- Build output directory:

```text
dist
```

- Environment variables:

```text
VITE_API_BASE_URL=https://your-api-domain.example
VITE_DEMO_MODE=false
VITE_LOCAL_DEV_AUTH_ENABLED=false
VITE_DEBUG_USER_HEADER_ENABLED=false
```

## 보안 차이

`SPRING_PROFILES_ACTIVE=prod`에서는 다음이 기본 적용됩니다.

- 개발용 로그인 비활성화
- `X-User-Id` 디버그 헤더 비활성화
- Strava token AES-GCM 암호화
- OAuth `state` 검증
- HTTPS 세션 쿠키
- 프록시 헤더 처리

## 운영 전 확인

1. `https://api-domain/api/health`가 정상 응답하는지 확인합니다.
2. 프론트엔드에서 Strava 로그인 버튼을 누릅니다.
3. Strava 승인 후 프론트엔드 성공 화면으로 돌아오는지 확인합니다.
4. 활동 동기화를 최근 100개로 먼저 실행합니다.
5. 전체 동기화는 Strava rate limit과 Nominatim 제한을 고려해 천천히 실행합니다.

## Nominatim 주의

공개 Nominatim은 대량/상업 운영에 적합하지 않습니다. 데모 수준을 넘어서면 Mapbox, Geoapify, Google Geocoding, 자체 Nominatim 인스턴스 중 하나로 교체하는 것이 좋습니다.
