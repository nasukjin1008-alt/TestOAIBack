#!/bin/bash
# 로컬 개발 서버 실행
# 사용법: ./scripts/run-local.sh [jasypt_key]

export Amaranth10_JSPT="${1:-change_me}"

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR"

echo "로컬 서버 시작 (port: 8080, context: /oneai)"
./gradlew bootRun \
  --args="--spring.profiles.active=local" \
  -Dorg.gradle.jvmargs="-Xmx2g"
