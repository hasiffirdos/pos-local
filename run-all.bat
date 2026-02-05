@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

echo ============================================
echo    POS Local - Startup Script
echo ============================================
echo.


where git >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Please run this script as Administrator.
    echo.
    pause
    exit /b 1
)


if not exist ".git" (
    echo ERROR: This directory is not a Git repository.
    echo Please clone the repository first or initialize git.
    echo.
    pause
    exit /b 1
)

git fetch origin main >nul 2>&1

REM Check if application.yml has local changes
git diff --quiet backend/src/main/resources/application.yml >nul 2>&1
set APP_YML_CHANGED=%ERRORLEVEL%

REM Save local application.yml if modified
if %APP_YML_CHANGED% NEQ 0 (
    copy backend\src\main\resources\application.yml backend\src\main\resources\application.yml.local >nul 2>&1
)

REM Reset everything to remote
git reset --hard origin/main >nul 2>&1
git clean -fd >nul 2>&1

REM Restore local application.yml if it was modified
if %APP_YML_CHANGED% NEQ 0 (
    copy backend\src\main\resources\application.yml.local backend\src\main\resources\application.yml >nul 2>&1
    del backend\src\main\resources\application.yml.local >nul 2>&1
)



start "POS Backend" cmd /k "%~dp0run-backend.bat"
start "POS Frontend" cmd /k "%~dp0run-frontend.bat"

echo ============================================
echo    POS Local started successfully!
echo ============================================
echo.
echo Backend and Frontend are running in separate windows.
echo.
