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

## Virtualization support not detected 오류

Docker Desktop에서 아래 오류가 나오면 프로젝트 문제가 아니라 Windows/BIOS 가상화 설정 문제입니다.

```text
Virtualization support not detected
Docker Desktop failed to start because virtualisation support wasn't detected.
```

확인 순서:

1. 작업 관리자 열기
2. `성능` 탭 선택
3. `CPU` 선택
4. 오른쪽 아래의 `가상화` 값 확인

`사용 안 함`이면 BIOS/UEFI에서 Intel VT-x 또는 AMD-V/SVM을 켜야 합니다.

일반적인 BIOS/UEFI 메뉴 이름:

```text
Intel CPU: Intel Virtualization Technology, VT-x
AMD CPU: SVM Mode, AMD-V
```

설정 후 Windows를 완전히 재부팅합니다.

Windows 기능도 필요합니다. 관리자 PowerShell에서 확인합니다.

```powershell
dism /online /get-featureinfo /featurename:VirtualMachinePlatform
dism /online /get-featureinfo /featurename:Microsoft-Windows-Subsystem-Linux
```

꺼져 있다면 관리자 PowerShell에서 활성화합니다.

```powershell
dism /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
dism /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
wsl --install
wsl --set-default-version 2
```

그 다음 재부팅하고 Docker Desktop을 다시 실행합니다.

BIOS/UEFI를 변경할 수 없는 PC라면 Docker 대신 로컬 PostgreSQL을 직접 설치해서 개발할 수 있습니다. 이 프로젝트는 Docker가 필수는 아니고 PostgreSQL만 있으면 백엔드 실행이 가능합니다.

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
password: replace-with-local-docker-password
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
