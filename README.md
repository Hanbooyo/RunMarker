# StravaMate - Running Passport

Strava OAuth로 로그인한 사용자의 러닝 활동을 가져와, 시작 좌표 기준으로 달린 도시/국가를 기록하는 웹앱입니다.

## Backend

- Java 17
- Spring Boot 3
- Gradle
- PostgreSQL
- MyBatis
- Flyway

실제 Strava 계정 테스트 환경 구성은 [docs/TEST_ENV.md](docs/TEST_ENV.md)를 참고하세요.

실행:

```bash
docker compose up -d
.\gradlew.bat bootRun
```

테스트:

```bash
.\gradlew.bat test
```

### VS Code에서 Java 빌드하기

VS Code 자체가 Java를 빌드하는 것이 아니라, 로컬에 설치된 JDK와 Gradle을 VS Code가 호출합니다.

필수 설치:

- JDK 17
- Gradle Wrapper
- VS Code Extension Pack for Java
- Spring Boot Extension Pack
- Gradle for Java

설치 확인:

```powershell
java -version
.\gradlew.bat --version
```

VS Code에서 실행:

1. `F:\workspace\StravaMate_Passport` 폴더를 엽니다.
2. 추천 확장 설치 알림이 뜨면 설치합니다.
3. `Ctrl+Shift+P`를 누릅니다.
4. `Tasks: Run Task`를 선택합니다.
5. 아래 task 중 하나를 실행합니다.

```text
Backend: test
Backend: build
Backend: bootRun
Frontend: install
Frontend: test
Frontend: dev
```

현재 저장소에는 Gradle Wrapper(`gradlew.bat`)가 아직 없습니다. Gradle이 설치된 환경에서 아래 명령을 한 번 실행하면 Wrapper를 추가할 수 있습니다.

```powershell
gradle wrapper
```

Wrapper가 추가된 뒤에는 다음 명령을 사용할 수 있습니다.

```powershell
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat bootRun
```

필수 환경변수:

```text
DB_URL=jdbc:postgresql://localhost:5432/stravamate_passport
DB_USERNAME=stravamate
DB_PASSWORD=stravamate
STRAVA_CLIENT_ID=your-client-id
STRAVA_CLIENT_SECRET=your-client-secret
STRAVA_REDIRECT_URI=http://localhost:8080/api/auth/strava/callback
FRONTEND_BASE_URL=http://localhost:5173
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173
NOMINATIM_USER_AGENT=StravaMatePassport/0.1 local
```

주요 API:

```text
GET  /api/health
GET  /api/auth/strava/login
GET  /api/auth/strava/callback
GET  /api/auth/me
POST /api/auth/logout
POST /api/sync/activities
GET  /api/activities
GET  /api/activities/{id}
GET  /api/passport/summary
GET  /api/passport/countries
GET  /api/passport/cities
GET  /api/passport/map-markers
GET  /api/passport/recent-places
```

## Frontend

- Vue 3
- Vite
- Pinia
- Vue Router
- Axios
- Tailwind CSS
- Leaflet
- Vitest

실행:

```bash
cd frontend
npm install
copy .env.example .env
npm run dev
```

테스트 및 빌드:

```bash
cd frontend
npm run test
npm run build
```

프론트 환경변수:

```text
VITE_API_BASE_URL=http://localhost:8080
VITE_DEMO_MODE=false
```

## Demo Mode

백엔드, PostgreSQL, Strava OAuth 없이 프론트 화면만 빠르게 확인하려면 demo mode를 사용합니다.

PowerShell:

```powershell
cd frontend
copy .env.example .env
```

`.env`를 아래처럼 수정합니다.

```text
VITE_API_BASE_URL=http://localhost:8080
VITE_DEMO_MODE=true
```

실행:

```powershell
npm install
npm run dev
```

브라우저에서 접속:

```text
http://127.0.0.1:5173/passport
```

demo mode에서는 다음 데이터가 mock으로 표시됩니다.

- Running Passport 요약
- 국가/도시 스탬프
- Leaflet 지도 마커
- 최근 방문 도시
- 활동 목록/상세
- 동기화 결과
```

## OAuth 흐름

1. 프론트에서 `GET /api/auth/strava/login`으로 이동합니다.
2. 백엔드가 Strava authorization URL로 리다이렉트합니다.
3. Strava callback에서 authorization code를 token으로 교환합니다.
4. `users`, `strava_tokens`를 upsert합니다.
5. 백엔드 세션에 사용자 ID를 저장합니다.
6. 프론트 라우터는 보호 라우트 진입 시 `/api/auth/me`로 세션을 확인합니다.

로컬 디버깅 중에는 `Local User ID` 또는 `X-User-Id` fallback을 사용할 수 있습니다.

## 동기화 흐름

1. `POST /api/sync/activities` 호출
2. access token 만료 시 refresh token으로 갱신
3. Strava `/athlete/activities`를 page/per_page 방식으로 조회
4. 최근 100개 중 `Run` + `start_latlng` 있는 활동만 저장
5. 중복 활동은 `user_id + strava_activity_id` unique 제약으로 방지
6. Nominatim reverse geocoding으로 도시/국가 판별
7. `visited_places`, `activity_places` 집계 갱신

## Reverse Geocoding

MVP는 Nominatim을 사용합니다.

- 좌표를 기본 소수점 3자리로 반올림해 `geocoding_cache`에 저장합니다.
- 같은 근처 좌표는 외부 API를 다시 호출하지 않습니다.
- 공개 Nominatim은 운영용 대량 호출에 적합하지 않습니다.
- 운영 전 Mapbox, Google Geocoding, 자체 Nominatim 인스턴스를 검토해야 합니다.

## 확인 SQL

```sql
SELECT id, name, city_name, region_name, country_name, country_code, geocoded_at
FROM activities
ORDER BY start_date DESC;

SELECT city_name, region_name, country_name, activity_count, total_distance_meters
FROM visited_places
ORDER BY activity_count DESC;

SELECT *
FROM sync_logs
ORDER BY started_at DESC;
```

## 현재 유의사항

- 백엔드 token 암호화는 `TokenCipher`로 구조만 분리되어 있고 현재 구현은 `NoOpTokenCipher`입니다.
- 운영 전 access token/refresh token 암호화가 필요합니다.
- MVP의 도시/국가 판별은 활동 시작 좌표 기준입니다.
- polyline/streams 기반 경로 전체 분석은 2차 버전 범위입니다.
