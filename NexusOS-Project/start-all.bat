@echo off
cd /d "%~dp0"

echo Starting NexusOS...
echo.

echo [1/2] Main Service (8080)...
start "NexusOS-Main" cmd /k "cd /d %~dp0backend\demo && mvn spring-boot:run"

echo [2/2] Game Service (8081)...
start "NexusOS-Game" cmd /k "cd /d %~dp0backend\game-service && mvn spring-boot:run"

echo.
echo Main:    http://localhost:8080
echo Game:    http://localhost:8081
echo.
pause
