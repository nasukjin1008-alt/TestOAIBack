#!/bin/bash
# OneAI 빌드 자동화 스크립트

set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR"

echo "=== [1/3] 컴파일 ==="
./gradlew compileJava -q

echo "=== [2/3] 테스트 실행 ==="
./gradlew test -q

echo "=== [3/3] JAR 패키징 ==="
./gradlew bootJar -q

echo ""
echo "빌드 완료: $(ls -lh build/libs/oneai*.jar 2>/dev/null || echo 'JAR 파일 없음')"
