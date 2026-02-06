@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

echo ============================================
echo    POS Local - Startup Script
echo ============================================
echo.

where git >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Git is not installed or not in PATH.
    pause
    exit /b 1
)

if not exist ".git" (
    echo ERROR: This directory is not a Git repository.
    pause
    exit /b 1
)

REM Stash any local changes
git stash >nul 2>&1

REM Pull latest from remote
git pull origin main >nul 2>&1

REM Restore stashed changes
git stash pop >nul 2>&1

start "POS Backend" cmd /k "%~dp0run-backend.bat"
start "POS Frontend" cmd /k "%~dp0run-frontend.bat"

echo ============================================
echo    POS Local started successfully!
echo ============================================
echo.
echo Backend and Frontend are running in separate windows.
echo.
