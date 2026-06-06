#!/bin/bash

echo "===================================="
echo "  NexusOS - 启动所有服务"
echo "===================================="

# 启动主服务 (8080)
echo "[1/2] 启动主服务 (8080)..."
cd backend/demo
mvn spring-boot:run &
DEMO_PID=$!

# 启动游戏服务 (8081)
echo "[2/2] 启动游戏服务 (8081)..."
cd ../game-service
mvn spring-boot:run &
GAME_PID=$!

echo ""
echo "主服务: http://localhost:8080"
echo "游戏服务: http://localhost:8081"
echo ""
echo "按 Ctrl+C 停止所有服务"

trap "kill $DEMO_PID $GAME_PID 2>/dev/null; exit" INT
wait
