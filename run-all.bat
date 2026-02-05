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

git stash push -m "local-config" -- backend/src/main/resources/application.yml backend/build/resources/main/application.yml >nul 2>&1

git reset --hard origin/main >nul 2>&1

git clean -fd >nul 2>&1

git stash pop >nul 2>&1



start "POS Backend" cmd /k "%~dp0run-backend.bat"
start "POS Frontend" cmd /k "%~dp0run-frontend.bat"

echo ============================================
echo    POS Local started successfully!
echo ============================================
echo.
echo Backend and Frontend are running in separate windows.
echo.
