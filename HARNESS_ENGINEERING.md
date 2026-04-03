# OneAI 하네스 엔지니어링 가이드

> **프로젝트**: amaranth10-oneai (OneAI Backend Service)  
> **작성일**: 2026-04-03  
> **도구**: Claude Code Harness Engineering

---

## 목차

1. [요구사항 및 환경 확인](#1-요구사항-및-환경-확인)
2. [프로젝트 구조 설계 및 초기화](#2-프로젝트-구조-설계-및-초기화)
3. [개발 및 배포 파이프라인 설계](#3-개발-및-배포-파이프라인-설계)
4. [개발/배포 자동화 구현](#4-개발배포-자동화-구현)
5. [산출물(보고서) 정리](#5-산출물보고서-정리)

---

## 1. 요구사항 및 환경 확인

### 1.1 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | OneAI Backend Service |
| 목적 | 엔터프라이즈 멀티모델 AI 통합 백엔드 플랫폼 |
| 빌드 도구 | Gradle 7.1.1 |
| 언어 | Java 1.8 |
| 프레임워크 | Spring Boot 2.5.1 |
| 서버 포트 | 80 (context-path: `/oneai`) |

### 1.2 기능 요구사항

| 기능 | 구현 상태 |
|------|-----------|
| 멀티 AI 모델 연동 (GPT, Claude, 자체 LLM) | 구현됨 |
| WebSocket 기반 실시간 스트리밍 | 구현됨 |
| RAG (Retrieval Augmented Generation) | 구현됨 |
| 이미지 생성 (DALL-E) | 구현됨 |
| 음성 인식 (Whisper STT) | 구현됨 |
| AI 사용량 통계 및 비용 추적 | 구현됨 |
| 프롬프트 갤러리 관리 | 구현됨 |
| 문서 인덱싱 및 검색 | 구현됨 |

### 1.3 외부 의존 인프라

| 인프라 | 버전/위치 | 용도 |
|--------|-----------|------|
| MariaDB | 10.x / `14.41.2.189:13306` | 주 데이터베이스 |
| Elasticsearch | 7.9.0 / 8.7.1 | RAG 문서 검색 |
| OpenSearch | 2.19.0 | 대화 이력 검색 |
| Redis | `14.41.2.189:16384` | 세션/캐시 |
| Kafka | `event01A01` 토픽 | 이벤트 발행 |
| AWS Bedrock | SDK 2.25.54 | Claude 모델 접근 |

### 1.4 개발 환경 사전 조건

```bash
# 필수 환경 변수
export Amaranth10_JSPT=<jasypt_encryption_key>   # 프로퍼티 복호화 키

# 로컬 개발 시 필요한 서비스
- MariaDB (127.0.0.1:3306)
- Gradle 7.1.1+
- JDK 1.8

# 내부 Maven 저장소 접근
- http://14.41.55.45:30000/nexus/  (Duzon 내부망)
```

### 1.5 Claude Code 하네스 환경 요구사항

```json
// ~/.claude/settings.json 권장 설정
{
  "hooks": {
    "PreToolCall": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "echo '[Harness] Bash 명령 실행 감지: ' $CLAUDE_TOOL_INPUT"
          }
        ]
      }
    ],
    "PostToolCall": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          {
            "type": "command",
            "command": "cd /Users/naseokjin/Source/amaranth10-oneai && ./gradlew compileJava 2>&1 | tail -5"
          }
        ]
      }
    ]
  }
}
```

---

## 2. 프로젝트 구조 설계 및 초기화

### 2.1 전체 아키텍처 구조

```
amaranth10-oneai/
├── config/                        # 외부 설정 파일 (배포 환경별)
│   ├── application.yml            # 공통 설정 (DB, Redis, ES, Kafka)
│   └── application-klago.yml      # 검색 엔진 설정
│
├── src/main/java/co/dzone/
│   ├── OneApplication.java        # Spring Boot 진입점
│   ├── config/                    # 설정 빈 (24개)
│   │   ├── RedisConfig.java
│   │   ├── WebSocketConfig.java
│   │   ├── DataSourceConfig.java
│   │   └── elasticsearch/
│   │
│   ├── framework/                 # 공통 프레임워크 (44개 파일)
│   │   ├── annotation/            # @AiCalculate (비용 자동 계산)
│   │   ├── aspect/                # AOP 횡단 관심사
│   │   ├── constant/              # 상수 정의
│   │   ├── exception/             # 전역 예외 처리
│   │   ├── filter/                # 요청/응답 필터
│   │   ├── security/              # 보안 컨텍스트
│   │   └── util/                  # 공통 유틸리티 (15+)
│   │
│   └── oneai/
│       ├── web/                   # REST 컨트롤러 (21개)
│       │   ├── ai/                # AI 메인 엔드포인트
│       │   ├── socket/            # WebSocket 핸들러
│       │   ├── statistics/        # 사용량 통계
│       │   └── ...
│       │
│       ├── service/               # 비즈니스 로직 (136개 파일)
│       │   ├── ai/                # OpenAiService, ClaudeService
│       │   ├── elasticsearch/     # ES 클라이언트 & 레포지토리
│       │   ├── rag/               # RAG 구현체
│       │   ├── aistatistics/      # 통계 & 비용 계산
│       │   └── ...
│       │
│       └── dto/                   # 데이터 전송 객체
│
├── src/main/resources/
│   ├── mapper/                    # MyBatis XML 매퍼 (23개)
│   ├── elasticsearch/             # ES 인덱스 매핑 (3개)
│   ├── opensearch/                # OpenSearch 인덱스 설정 (6개)
│   ├── dbPatch/DDL|DML/           # DB 마이그레이션 스크립트
│   └── config/
│       ├── application-oneai.yml
│       ├── application-local.yml
│       └── mybatis-config.xml
│
├── build.gradle                   # Gradle 빌드 설정
├── settings.gradle                # 프로젝트명: oneaiBoot
├── .gitlab-ci.yml                 # GitLab CI/CD 파이프라인
└── log4j2.xml                    # 로깅 설정
```

### 2.2 도메인 레이어 설계

```
WebSocket 스트리밍 흐름:
  클라이언트 → /ws/socket/*
    └── StreamingHandler
          └── RequestDispatcher
                ├── GptRequestStrategy     (GPT-4, GPT-3.5)
                ├── ClaudeRequestStrategy  (Anthropic/AWS Bedrock)
                └── SLLMRequestStrategy    (Exaone, LLaMA, DSM)

REST API 흐름:
  클라이언트 → /oneai/api/**
    └── Controller (21개)
          └── Service (136개 Java 파일)
                └── Repository (MyBatis Mapper)
                      └── MariaDB
```

### 2.3 로컬 개발 초기화 절차

```bash
# 1. 저장소 클론
git clone <repository-url> amaranth10-oneai
cd amaranth10-oneai

# 2. gRPC 소스 생성 (README 지시사항)
./gradlew generateProto

# 3. 로컬 설정 파일 준비
cp src/main/resources/config/application-local.yml config/application.yml
# application.yml에서 로컬 DB 정보 수정

# 4. 환경 변수 설정
export Amaranth10_JSPT=<jasypt_key>

# 5. 빌드 확인
./gradlew compileJava

# 6. 로컬 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

## 3. 개발 및 배포 파이프라인 설계

### 3.1 GitLab CI/CD 브랜치 전략

```
main/master        →  운영(Production) 배포
sqa                →  SQA(통합 테스트) 배포
devqa              →  개발 QA 배포
feature/*          →  기능 개발 (로컬 검증 후 devqa merge)
hotfix/*           →  긴급 패치 (master 직접 merge)
```

### 3.2 기존 `.gitlab-ci.yml` 파이프라인 단계

```yaml
stages:
  - build      # JAR 아티팩트 생성
  - devqa      # Dev-QA Docker 이미지 빌드 및 배포
  - sqa        # SQA Docker 이미지 빌드 및 배포
  - master     # Production Docker 이미지 빌드 및 배포

# 각 스테이지 트리거 브랜치
build:   [devqa, sqa, master]
devqa:   [devqa]
sqa:     [sqa]
master:  [master]
```

### 3.3 배포 파이프라인 흐름도

```
개발자 Push
    │
    ▼
GitLab CI Trigger
    │
    ├─[devqa branch]──► Gradle Build → Docker Image → DevQA 서버 배포
    │
    ├─[sqa branch]────► Gradle Build → Docker Image → SQA 서버 배포
    │
    └─[master branch]─► Gradle Build → Docker Image → Production 서버 배포
                              │
                              ▼
                    oneai.jar (build/libs/)
                              │
                              ▼
                    java -cp oneai.jar OneApplication
```

### 3.4 Docker 컨테이너 구성

```dockerfile
# 권장 Dockerfile 구조
FROM openjdk:8-jre-slim

WORKDIR /app

# 외부 설정 파일 마운트 경로
VOLUME /app/config

COPY build/libs/oneai.jar /app/oneai.jar

ENV Amaranth10_JSPT=""
ENV SPRING_PROFILES_ACTIVE="oneai"

ENTRYPOINT ["java", "-jar", "/app/oneai.jar", \
  "--spring.config.additional-location=file:/app/config/"]
```

### 3.5 환경별 설정 전략

| 환경 | 프로파일 | DB | ES | 비고 |
|------|----------|----|----|------|
| 로컬 | `local` | 127.0.0.1:3306 | 없음 | 개발자 PC |
| DevQA | `oneai` | 내부망 DB | ES 8.x | config/ 외부 마운트 |
| SQA | `oneai` | 내부망 DB | ES 8.x | config/ 외부 마운트 |
| Production | `oneai` | 14.41.2.189:13306 | 10.82.6.144:19204 | config/ 외부 마운트 |

---

## 4. 개발/배포 자동화 구현

### 4.1 Claude Code Hooks 설정

Claude Code 하네스를 활용한 자동화 훅을 `~/.claude/settings.json`에 설정합니다.

```json
{
  "hooks": {
    "PreToolCall": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "echo '[OneAI Hook] 명령 실행 전 검증 중...' >&2"
          }
        ]
      }
    ],
    "PostToolCall": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          {
            "type": "command",
            "command": "cd /Users/naseokjin/Source/amaranth10-oneai && ./gradlew compileJava -q 2>&1 | grep -E 'error:|BUILD' || true"
          }
        ]
      }
    ],
    "Stop": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "echo '[OneAI] Claude Code 세션 종료. 변경 사항을 커밋하세요.' >&2"
          }
        ]
      }
    ]
  }
}
```

### 4.2 하네스 자동화 스크립트

#### 빌드 자동화 (`scripts/build.sh`)

```bash
#!/bin/bash
# OneAI 빌드 자동화 스크립트

set -e

PROJECT_DIR="/Users/naseokjin/Source/amaranth10-oneai"
cd "$PROJECT_DIR"

echo "=== [1/4] gRPC 소스 생성 ==="
./gradlew generateProto -q

echo "=== [2/4] 컴파일 ==="
./gradlew compileJava -q

echo "=== [3/4] 테스트 실행 ==="
./gradlew test -q

echo "=== [4/4] JAR 패키징 ==="
./gradlew bootJar -q

echo "빌드 완료: $(ls -lh build/libs/oneai.jar)"
```

#### DB 패치 자동화 확인 (`scripts/check-dbpatch.sh`)

```bash
#!/bin/bash
# 적용 대기 중인 DB 패치 확인

PATCH_DIR="src/main/resources/dbPatch"

echo "=== DDL 패치 현황 ==="
ls -la "$PATCH_DIR/DDL/"

echo ""
echo "=== DML 패치 현황 ==="
ls -la "$PATCH_DIR/DML/"

echo ""
echo "최근 스케줄 패치:"
ls -la src/main/resources/schedule/
```

#### 로컬 실행 자동화 (`scripts/run-local.sh`)

```bash
#!/bin/bash
# 로컬 개발 서버 실행

export Amaranth10_JSPT="${1:-your_jasypt_key}"
export SPRING_PROFILES_ACTIVE="local"

cd /Users/naseokjin/Source/amaranth10-oneai

./gradlew bootRun \
  --args="--spring.profiles.active=local" \
  -Dorg.gradle.jvmargs="-Xmx2g"
```

### 4.3 Claude Code 스케줄 자동화

Claude Code의 `/schedule` 기능을 활용한 정기 작업:

```bash
# 매일 오전 9시: 빌드 상태 점검
/schedule "매일 09:00 OneAI 프로젝트 빌드 상태 점검 및 의존성 취약점 확인"

# 매주 월요일: DB 패치 현황 리포트
/schedule "매주 월요일 09:00 amaranth10-oneai dbPatch 디렉토리 신규 패치 확인 및 보고"
```

### 4.4 GitLab CI 자동화 강화 (권장)

```yaml
# .gitlab-ci.yml 개선안
stages:
  - validate
  - build
  - test
  - deploy

variables:
  GRADLE_OPTS: "-Dorg.gradle.daemon=false"
  GRADLE_USER_HOME: "$CI_PROJECT_DIR/.gradle"

cache:
  key: "$CI_COMMIT_REF_SLUG"
  paths:
    - .gradle/

validate:
  stage: validate
  script:
    - ./gradlew compileJava
  rules:
    - if: '$CI_PIPELINE_SOURCE == "merge_request_event"'

build:
  stage: build
  script:
    - ./gradlew bootJar
  artifacts:
    paths:
      - build/libs/oneai.jar
    expire_in: 1 week
  rules:
    - if: '$CI_COMMIT_BRANCH =~ /^(devqa|sqa|master)$/'

test:
  stage: test
  script:
    - ./gradlew test
  artifacts:
    reports:
      junit: build/test-results/test/*.xml
  rules:
    - if: '$CI_COMMIT_BRANCH =~ /^(devqa|sqa|master)$/'

deploy-devqa:
  stage: deploy
  script:
    - docker build -t oneai:devqa-$CI_COMMIT_SHORT_SHA .
    - docker push $REGISTRY/oneai:devqa-$CI_COMMIT_SHORT_SHA
  environment:
    name: devqa
  rules:
    - if: '$CI_COMMIT_BRANCH == "devqa"'

deploy-production:
  stage: deploy
  script:
    - docker build -t oneai:$CI_COMMIT_TAG .
    - docker push $REGISTRY/oneai:$CI_COMMIT_TAG
  environment:
    name: production
  when: manual
  rules:
    - if: '$CI_COMMIT_BRANCH == "master"'
```

### 4.5 모니터링 자동화

```bash
# Spring Actuator 헬스 체크 자동화
curl -s http://localhost/oneai/actuator/health | jq .

# AI 통계 엔드포인트 모니터링
curl -s http://localhost/oneai/api/statistics/summary \
  -H "Authorization: Bearer $TOKEN" | jq .

# Kafka 이벤트 발행 확인
kafka-console-consumer.sh \
  --bootstrap-server $KAFKA_HOST \
  --topic event01A01 \
  --from-beginning \
  --max-messages 10
```

---

## 5. 산출물(보고서) 정리

### 5.1 프로젝트 현황 요약

| 구분 | 내용 |
|------|------|
| 총 Java 파일 수 | 약 200+ (framework 44, service 136, web 21+) |
| MyBatis 매퍼 | 23개 XML |
| REST 엔드포인트 | 21개 컨트롤러 |
| 지원 AI 모델 | 40+ (GPT, Claude, Exaone, LLaMA, DSM 등) |
| 인프라 의존성 | MariaDB, ES, OpenSearch, Redis, Kafka, AWS Bedrock |
| CI/CD 환경 | GitLab CI (devqa / sqa / production 3단계) |

### 5.2 기술 스택 매트릭스

```
백엔드 레이어
├── 프레임워크   : Spring Boot 2.5.1 + Spring WebFlux + WebSocket
├── ORM         : MyBatis 2.2.0 (HikariCP 커넥션 풀)
├── 검색         : Elasticsearch 7.9/8.7 + OpenSearch 2.x
├── 캐시/메시지  : Redis (Klago) + Kafka
├── AI 통합      : OpenAI SDK + AWS Bedrock SDK
├── 보안         : Jasypt 3.0.3 + Bouncy Castle 1.78
├── 매핑         : MapStruct 1.6.3 + Lombok
└── 빌드         : Gradle 7.1.1
```

### 5.3 하네스 엔지니어링 적용 결과

| 자동화 항목 | 도구 | 효과 |
|------------|------|------|
| 코드 변경 후 컴파일 검증 | PostToolCall Hook | 즉각적 빌드 오류 감지 |
| DB 패치 현황 정기 확인 | Claude Code Schedule | 누락 패치 방지 |
| 빌드 파이프라인 자동화 | GitLab CI + Shell Script | 수동 배포 오류 제거 |
| 환경별 설정 분리 | Spring Profile + 외부 config | 환경 혼용 방지 |
| 테스트 자동화 | JUnit 5 + CI Test Stage | 회귀 오류 조기 발견 |

### 5.4 개선 권고사항

#### 단기 (즉시 적용 권장)

- [ ] **테스트 커버리지 강화**: 현재 단위 테스트 부족 → JUnit5 기반 Service 레이어 테스트 추가
- [ ] **로컬 Docker Compose 작성**: MariaDB + Redis + Kafka 로컬 환경 구성 자동화
- [ ] **`.env` 파일 템플릿**: 환경 변수 관리 표준화 (`.env.example` 제공)

#### 중기 (스프린트 내 계획)

- [ ] **Java 버전 업그레이드**: Java 8 → Java 17 LTS 마이그레이션
- [ ] **Spring Boot 업그레이드**: 2.5.1 → 3.x (보안 패치 및 성능 개선)
- [ ] **Elasticsearch 버전 통일**: 7.9 / 8.7 이중 지원 → 8.x 단일화
- [ ] **비용 알림 자동화**: AI 토큰 비용 임계치 초과 시 Kafka 이벤트 → Slack 알림

#### 장기 (아키텍처 개선)

- [ ] **설정 서버 도입**: Spring Cloud Config Server로 중앙 집중 설정 관리
- [ ] **API Gateway 추가**: 인증/인가, Rate Limiting, 로드 밸런싱
- [ ] **관찰 가능성(Observability) 강화**: Prometheus + Grafana 연동 (Actuator 지표 활용)

### 5.5 주요 리스크 및 대응

| 리스크 | 심각도 | 대응 방안 |
|--------|--------|-----------|
| Java 8 EOL | 높음 | Java 17 마이그레이션 계획 수립 |
| Spring Boot 2.5.1 보안 취약점 | 높음 | 3.x 업그레이드 또는 패치 적용 |
| 내부 Maven 저장소 단일 장애점 | 중간 | 미러 저장소 또는 캐시 구성 |
| Jasypt 키 노출 | 높음 | AWS Secrets Manager 또는 Vault 도입 검토 |
| ES 7.9 / 8.7 이중 유지보수 | 중간 | 8.x 단일화 로드맵 수립 |

### 5.6 하네스 엔지니어링 작업 로그

```
[2026-04-03] 초기 프로젝트 분석 완료
  - 전체 소스 구조 파악 (200+ Java 파일)
  - 인프라 의존성 목록 확보
  - 기존 CI/CD 파이프라인(.gitlab-ci.yml) 분석

[2026-04-03] 하네스 설정 설계
  - Claude Code Hooks 구성 (PreToolCall, PostToolCall, Stop)
  - 빌드/배포 자동화 스크립트 초안 작성
  - GitLab CI 개선안 작성

[2026-04-03] 문서화 완료
  - 환경 요구사항 정리
  - 프로젝트 구조 다이어그램 작성
  - 배포 파이프라인 설계 완료
  - 개선 권고사항 도출
```

---

*이 문서는 Claude Code 하네스 엔지니어링을 활용하여 자동 생성/관리됩니다.*  
*최종 업데이트: 2026-04-03*
