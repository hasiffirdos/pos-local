@echo off
setlocal
cd /d "%~dp0"
if not exist data mkdir data
set POS_DB_PATH=.\data\pos.db
set GRADLE_OPTS=-Xms64m -Xmx256m -XX:MaxMetaspaceSize=128m

cd /d "%~dp0backend"
call .\gradlew.bat --no-daemon clean build -x test
call .\gradlew.bat --no-daemon bootRun
