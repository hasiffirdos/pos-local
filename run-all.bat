@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

echo ============================================
echo    POS Local - Startup Script
echo ============================================
echo.

REM ============================================
REM Step 1: Check if Git is installed
REM ============================================
echo [1/3] Checking if Git is installed...
where git >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Please run this script as Administrator.
    echo.
    pause
    exit /b 1
)
echo Git is installed.
echo.

REM ============================================
REM Step 2: Pull latest changes from Git
REM ============================================
echo [2/3] Pulling latest changes from repository...

REM Check if current directory is a git repository
if not exist ".git" (
    echo ERROR: This directory is not a Git repository.
    echo Please clone the repository first or initialize git.
    echo.
    pause
    exit /b 1
)

REM Fetch latest from remote
echo Fetching latest from origin...
git fetch origin main

REM Reset to remote main (discards all local changes, prioritizes remote)
echo Resetting to origin/main (remote takes priority)...
git reset --hard origin/main

REM Clean untracked files that might conflict
git clean -fd 2>nul

echo Pull complete!
echo.

REM ============================================
REM Step 3: Start the application
REM ============================================
echo [3/3] Starting POS application...
echo.

start "POS Backend" cmd /k "%~dp0run-backend.bat"
start "POS Frontend" cmd /k "%~dp0run-frontend.bat"

echo ============================================
echo    POS Local started successfully!
echo ============================================
echo.
echo Backend and Frontend are running in separate windows.
echo.
