# Cloudflare Pages + Render + Neon 배포 절차

이 문서는 `hosting-prod` 브랜치를 기준으로 데모 배포를 진행하는 순서입니다.

## 1. Neon Postgres 생성

1. Neon에서 새 프로젝트를 생성합니다.
2. PostgreSQL connection string을 확인합니다.
3. JDBC URL 형태로 변환합니다.

예:

```text
postgresql://USER:PASSWORD@HOST/DB?sslmode=require
```

백엔드에는 아래처럼 나눠 넣습니다.

```text
DB_URL=jdbc:postgresql://HOST/DB?sslmode=require
DB_USERNAME=USER
DB_PASSWORD=PASSWORD
```

## 2. Render Backend 생성

권장 방식은 저장소의 `render.yaml`을 사용하는 Blueprint 배포입니다.

Render에서 GitHub 저장소 `Hanbooyo/RunMarker`를 연결하고 브랜치는 `hosting-prod`를 선택합니다.

필수 환경변수:

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://HOST/DB?sslmode=require
DB_USERNAME=USER
DB_PASSWORD=PASSWORD
FRONTEND_BASE_URL=https://YOUR_FRONTEND.pages.dev
CORS_ALLOWED_ORIGINS=https://YOUR_FRONTEND.pages.dev
STRAVA_CLIENT_ID=...
STRAVA_CLIENT_SECRET=...
STRAVA_REDIRECT_URI=https://YOUR_BACKEND.onrender.com/api/auth/strava/callback
TOKEN_CIPHER=aes-gcm
TOKEN_ENCRYPTION_KEY=...
DEV_AUTH_ENABLED=false
DEBUG_USER_HEADER_ENABLED=false
NOMINATIM_USER_AGENT=RunMarker/0.1 demo contact@example.com
```

`TOKEN_ENCRYPTION_KEY` 생성 예:

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

배포 후 확인:

```text
https://YOUR_BACKEND.onrender.com/api/health
```

## 3. Cloudflare Pages Frontend 생성

Cloudflare Pages에서 GitHub 저장소를 연결합니다.

설정:

```text
Branch: hosting-prod
Root directory: frontend
Build command: npm ci && npm run build
Build output directory: dist
```

환경변수:

```text
VITE_API_BASE_URL=https://YOUR_BACKEND.onrender.com
VITE_DEMO_MODE=false
VITE_LOCAL_DEV_AUTH_ENABLED=false
VITE_DEBUG_USER_HEADER_ENABLED=false
```

`frontend/public/_redirects`가 포함되어 있어 Vue Router 경로에서 새로고침해도 `index.html`로 fallback됩니다.

## 4. Strava App 설정

Strava API Application의 Authorization Callback Domain에는 도메인만 입력합니다.

예:

```text
YOUR_BACKEND.onrender.com
```

백엔드 환경변수의 `STRAVA_REDIRECT_URI`는 전체 URL입니다.

```text
https://YOUR_BACKEND.onrender.com/api/auth/strava/callback
```

## 5. 최종 테스트

1. Cloudflare Pages URL에 접속합니다.
2. Strava 로그인 버튼을 누릅니다.
3. 승인 후 프론트엔드로 돌아오는지 확인합니다.
4. 최근 100개 동기화를 먼저 실행합니다.
5. Passport, Countries, Cities, Activities 화면을 확인합니다.

## 6. 주의사항

- Render 무료 인스턴스는 첫 요청이 느릴 수 있습니다.
- 무료 DB/백엔드는 데모용입니다. 장기 운영 전에는 백업, 모니터링, 비용 한도를 따로 확인해야 합니다.
- 공개 Nominatim은 대량 호출에 적합하지 않습니다. 사용자 수가 늘면 Mapbox 또는 Geoapify 같은 서비스로 교체하는 것이 좋습니다.
