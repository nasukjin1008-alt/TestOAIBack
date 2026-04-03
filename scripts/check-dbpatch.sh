#!/bin/bash
# 적용 대기 중인 DB 패치 확인

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PATCH_DIR="$PROJECT_DIR/src/main/resources/dbPatch"

echo "=== DDL 패치 현황 ==="
ls -la "$PATCH_DIR/DDL/" 2>/dev/null || echo "DDL 패치 없음"

echo ""
echo "=== DML 패치 현황 ==="
ls -la "$PATCH_DIR/DML/" 2>/dev/null || echo "DML 패치 없음"
