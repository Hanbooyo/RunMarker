# Strava Real Account Test

실제 Strava 계정으로 테스트할 때는 demo mode를 끄고 백엔드에 Strava 앱 정보를 설정합니다.

## 1. Strava 앱 설정

Strava API Application에서 아래 값을 확인합니다.

- Client ID
- Client Secret
- Authorization Callback Domain

로컬 테스트 callback URL:

```text
http://localhost:8080/api/auth/strava/callback
```

Strava 앱의 callback domain에는 일반적으로 아래 값을 넣습니다.

```text
localhost
```

## 2. 백엔드 로컬 환경변수 파일

프로젝트 루트에 `.env.backend.local` 파일을 만듭니다.

```text
STRAVA_CLIENT_ID=your-client-id
STRAVA_CLIENT_SECRET=your-client-secret
STRAVA_REDIRECT_URI=http://localhost:8080/api/auth/strava/callback
FRONTEND_BASE_URL=http://localhost:5173
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173
NOMINATIM_USER_AGENT=StravaMatePassport/0.1 local
SESSION_TIMEOUT=60m
```

`.env.backend.local`은 git에 올리지 않습니다.

## 3. 프론트 demo mode 끄기

`frontend/.env`:

```text
VITE_API_BASE_URL=http://localhost:8080
VITE_DEMO_MODE=false
```

## 4. 서비스 실행

백엔드:

```powershell
scripts\dev\run-backend-local.bat
```

프론트엔드:

```powershell
scripts\dev\run-frontend-dev.bat
```

## 5. 로그인 테스트

브라우저에서 아래 주소로 접속합니다.

```text
http://127.0.0.1:5173/login
```

Strava 로그인 후 권한 요청 화면에서 활동 읽기 권한을 승인합니다.

`STRAVA_CLIENT_ID` 또는 `STRAVA_CLIENT_SECRET`이 비어 있거나 `your-...` placeholder 그대로이면 `/login/error?error=missing_strava_config`로 이동합니다.

현재 앱은 OAuth scope로 아래 값을 요청합니다.

```text
read,activity:read_all
```

`activity:read_all`은 `Only You` 활동까지 읽기 위해 필요합니다. 공개 활동만 테스트할 경우에도 MVP에서는 동일 scope를 사용합니다.

## 6. 동기화 테스트

로그인 성공 후:

1. `/sync` 화면으로 이동
2. 평소에는 `최근 100개 동기화` 실행
3. 과거 전체 활동을 가져오려면 `전체 기간 동기화` 실행
3. `/passport` 화면에서 국가/도시/지도 확인

동기화 API:

```text
POST /api/sync/activities?mode=recent
POST /api/sync/activities?mode=full
```

`recent`는 최신 활동 100개만 요청합니다. `full`은 Strava `/athlete/activities` 페이지를 끝까지 조회합니다.

전체 기간 동기화는 활동 수, Nominatim reverse geocoding 호출 수, Strava rate limit 상태에 따라 오래 걸릴 수 있습니다. Strava rate limit에 가까워지면 동기화는 `PARTIAL_SUCCESS`로 멈추고, 나중에 다시 실행하면 중복 저장 없이 이어서 처리합니다.

DB 확인:

```sql
SELECT id, email, strava_athlete_id, display_name FROM users;
SELECT id, name, type, distance_meters, city_name, country_name FROM activities ORDER BY start_date DESC;
SELECT city_name, country_name, activity_count, total_distance_meters FROM visited_places ORDER BY activity_count DESC;
SELECT status, message, started_at, finished_at FROM sync_logs ORDER BY started_at DESC;
```

## 주의사항

- Strava access token, refresh token, client secret은 프론트엔드로 전달하지 않습니다.
- 실제 계정 테스트 전에는 `frontend/.env` 변경 후 Vite dev server를 재시작해야 합니다.
- 백엔드 `.env.backend.local` 변경 후에도 백엔드를 재시작해야 합니다.
- Nominatim reverse geocoding은 공개 서비스이므로 반복 대량 테스트는 피합니다.
- 로그인 세션 유지 시간은 기본 1시간입니다. 변경하려면 `SESSION_TIMEOUT` 값을 수정합니다.

## 로컬 개발용 로그인

Strava OAuth 없이 화면과 세션 흐름만 확인하려면 로그인 화면에서 `로컬 개발용 로그인`을 누릅니다.

이 기능은 `strava_athlete_id = -1`인 개발용 사용자를 DB에 만들고 세션에 로그인합니다. 실제 Strava token은 저장하지 않으므로 활동 동기화는 동작하지 않습니다.
