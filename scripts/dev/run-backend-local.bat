@echo off
setlocal

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "DB_URL=jdbc:postgresql://localhost:5432/stravamate_passport"
set "DB_USERNAME=stravamate"
set "DB_PASSWORD=stravamate"
set "FRONTEND_BASE_URL=http://localhost:5173"
set "CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173"
set "NOMINATIM_USER_AGENT=StravaMatePassport/0.1 local"

cd /d "%~dp0..\.."

if exist ".env.backend.local" (
    for /f "usebackq eol=# tokens=1,* delims==" %%A in (".env.backend.local") do (
        if not "%%A"=="" set "%%A=%%B"
    )
)

call gradlew.bat bootRun
