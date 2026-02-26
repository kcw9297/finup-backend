<div align=center>

# Finup

**주식 초보 대상 주식 경제 교육 플랫폼**

***

### Backend
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)

### Frontend
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=000)

### Infra / DevOps
![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socketdotio&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonwebservices&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)

### CI/CD & Tools
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)


### Period

![Period](https://img.shields.io/badge/-2025.11.27_~_2025.12.19-blue?style=for-the-badge)

</div>

***

## 📖 소개
**FinUp**은 금융 지식 학습과 실시간 시장 정보를 통합 제공하는 웹 플랫폼입니다.
금융 개념·용어를 학습하고, OpenAI 기반 **AI 퀴즈**로 학습 수준을 점검할 수 있습니다.

***

## 🔎 주요 서비스 소개

### 📚 금융 학습

- **금융 개념 학습** — 학습 모듈(Study) 조회 및 학습 진도 추적
- **금융 용어 사전** — 용어 검색·상세 조회 및 AI 시맨틱 검색 (코사인 유사도)
- **개인 단어장** — 관심 용어를 모아 관리하는 단어장(WordBook)
- **북마크** — 학습 콘텐츠 관심 목록 저장·관리

### 🤖 AI 퀴즈

- **GPT-4o-mini 기반** 금융 퀴즈 자동 생성
- 학습 모듈별 AI 맞춤 퀴즈 제공 및 즉시 결과 확인

### 📰 실시간 금융 뉴스

- **네이버 뉴스 API** 기반 실시간 금융 뉴스 목록 제공
- OpenAI 기반 뉴스 본문 **AI 요약** 및 키워드 분석 제공
- 주식 종목별 관련 뉴스 필터링

### 📈 주식 시장 데이터

- **KIS API** 연동 실시간 주가 조회
- 캔들차트·거래량 차트 등 인터랙티브 차트 시각화 (Lightweight Charts, Chart.js)
- KEXIM·OpenPortal API 기반 금융 지수·환율 등 주요 시장 지표 제공

### 🔐 인증 & 보안

- 이메일 인증 코드 기반 회원가입
- JWT + HttpOnly Cookie 기반 로그인 유지
- Spring Security 역할 기반 접근 제어 (USER / ADMIN)

### 🛠️ 관리자 기능

- 회원 목록 조회 및 관리 (엑셀 다운로드)
- 학습 모듈·금융 용어·영상 링크 CRUD
- 공지사항 작성·수정·삭제

***

## 📦 기술 스택

| Category | Stack |
|----------|--------|
| **Backend** | Gradle 8.x, Spring Boot 3.5.6, Spring Security, Spring Data JPA, MyBatis, JWT, Spring Mail |
| **Frontend** | React (Vite), JavaScript, Zustand, Material UI (MUI) |
| **Database** | MariaDB, Hibernate |
| **Auth** | Spring Security, BCrypt, Gmail SMTP |
| **AI** | Spring AI (OpenAI GPT-4o-mini) |
| **Storage** | AWS S3, AWS SDK v2 |
| **Infra / DevOps** | AWS (EC2, RDS, S3, ElastiCache, CloudFront), Redis, Docker, Nginx |
| **CI/CD** | GitHub Actions |
| **API Testing** | Postman |
| **External APIs** | KRX API, 한국수출입은행 API, 한국투자증권 API, 공공데이터포털 API, 네이버 뉴스 API, DART API, YouTube Data API v3 |
| **Java Version** | Java 17 |
***

## 📁 프로젝트 구조
### Backend (`finup/`)

```
finup/
├── src/main/java/app/finup/
│   ├── FinupApplication.java              # 애플리케이션 진입점 (@EnableScheduling)
│   │
│   ├── api/external/                      # 외부 API 클라이언트
│   │   ├── stock/                         # KIS 주식 API
│   │   ├── news/                          # 네이버 뉴스 API
│   │   ├── financialindex/                # KEXIM 금융 지수 API
│   │   ├── marketindex/                   # OpenPortal 시장 지수 API
│   │   └── youtube/                       # YouTube API
│   │
│   ├── layer/domain/                      # 도메인별 Controller·Service·Repository
│   │   ├── auth/                          # 인증·회원가입
│   │   ├── member/                        # 회원 관리
│   │   ├── stock/                         # 주식 데이터
│   │   ├── news/                          # 금융 뉴스
│   │   ├── study/                         # 학습 모듈
│   │   ├── studyword/                     # 학습 단어
│   │   ├── studyprogress/                 # 학습 진도
│   │   ├── quiz/                          # AI 퀴즈
│   │   ├── bookmark/                      # 북마크
│   │   ├── notice/                        # 공지사항
│   │   ├── words/                         # 금융 용어 사전
│   │   ├── memberWordbook/                # 개인 단어장
│   │   ├── videolink/                     # 학습 영상
│   │   ├── indicator/                     # 금융 지표 (스케줄러 포함)
│   │   └── uploadfile/                    # 파일 업로드 관리
│   │
│   ├── config/                            # Spring 설정
│   │   ├── SecurityConfig.java            # Security·JWT·CORS 설정
│   │   ├── RedisConfig.java               # Redis 세션 설정
│   │   ├── AWSConfig.java                 # AWS S3 설정
│   │   ├── MailConfig.java                # Gmail SMTP 설정
│   │   ├── WebClientConfig.java           # 외부 API HTTP 클라이언트
│   │   └── AsyncConfig.java               # 비동기·임베딩 스레드풀 설정
│   │
│   ├── infra/                             # 인프라·공통 서비스
│   │   ├── ai/                            # OpenAI Chat·Embedding Provider
│   │   ├── file/storage/                  # 파일 저장 추상화 (Local·S3)
│   │   ├── mail/                          # 이메일 서비스
│   │   └── redisson/                      # 분산 락·캐시
│   │
│   ├── security/                          # JWT 필터·핸들러·Provider
│   └── common/                            # 공통 예외처리·AOP·유틸
│
├── src/main/resources/
│   ├── application.yml                    # 메인 설정 (프로파일 분기)
│   └── settings/
│       ├── application-db.yml             # DB·JPA·HikariCP 설정
│       ├── application-web.yml            # Web·JWT·Multipart 설정
│       ├── application-mail.yml           # Gmail SMTP 설정
│       ├── application-security.yml       # OAuth2 설정 (미구현)
│       ├── application-ai.yml             # OpenAI 모델 설정
│       └── application-cache.yml          # Redis·Redisson 설정
│
├── docker-compose.yml                     # MariaDB + Redis
└── build.gradle
```

### Frontend (`finup-react/`)

```
finup-react/
├── src/
│   ├── base/                              # 공통 레이어
│   │   ├── components/                    # 공용 UI (Bar·Card·Modal·Layout·Icon)
│   │   ├── hooks/                         # 공통 훅 (로그인·북마크·학습진도·로그아웃)
│   │   ├── layouts/                       # 레이아웃 (Main·Sidebar·Empty)
│   │   ├── stores/                        # Zustand 전역 상태 (로그인·북마크·진도)
│   │   ├── routes/                        # 라우트 가드 (Protected·Guest)
│   │   ├── utils/                         # fetchUtils·downloadXlsx·mask 등
│   │   ├── provider/                      # SnackbarProvider
│   │   └── design/                        # MUI 테마 설정
│   │
│   └── features/                          # 기능별 모듈
│       ├── auth/                          # 로그인
│       ├── member/                        # 회원가입·관리 (엑셀·PDF 내보내기)
│       ├── mypage/                        # 마이페이지·북마크
│       ├── home/                          # 홈 (뉴스·환율·주식·워드클라우드)
│       ├── news/                          # 금융 뉴스
│       ├── stocks/                        # 주식 시장 (캔들·거래량 차트)
│       ├── study/                         # 학습 모듈
│       ├── studyword/                     # 학습 단어
│       ├── studyprogress/                 # 학습 진도
│       ├── word/                          # 금융 용어 사전 (시맨틱 검색)
│       ├── wordbook/                      # 개인 단어장
│       ├── quiz/                          # AI 퀴즈
│       ├── notice/                        # 공지사항
│       ├── videolink/                     # 학습 영상
│       └── admin/                         # 관리자
│
├── .env                                   # 개발 환경 변수
├── .env.production                        # 프로덕션 환경 변수
├── vite.config.js
└── package.json
```
---

## 🔑 Endpoints

> ✅ = 로그인 필요 · ❌ = 공개 · 🔒 = ADMIN 전용

### 인증 (`/auth`, `/api/auth`, `/public/api/members`)

| Method | URL                               | 인증 | Description              |
| ------ | --------------------------------- | ---- | ------------------------ |
| `GET`  | `/auth/me`                        | ✅   | 현재 로그인 사용자 조회  |
| `POST` | `/api/auth/csrf`                  | ❌   | CSRF 토큰 발급           |
| `POST` | `/api/auth/join/email`            | ❌   | 이메일 인증 코드 발송    |
| `POST` | `/api/auth/join/email/verify`     | ❌   | 이메일 인증 코드 확인    |
| `POST` | `/public/api/members/join`        | ❌   | 회원가입                 |

### 회원 (`/api/members`)

| Method  | URL                           | 인증 | Description        |
| ------- | ----------------------------- | ---- | ------------------ |
| `GET`   | `/api/members/list`           | 🔒   | 회원 목록 조회     |
| `GET`   | `/api/members/me/detail`      | ✅   | 내 정보 상세 조회  |
| `PATCH` | `/api/members/me/nickname`    | ✅   | 닉네임 변경        |
| `PATCH` | `/api/members/me/password`    | ✅   | 비밀번호 변경      |

### 금융 뉴스 (`/api/news`, `/public/api/news`)

| Method | URL                                    | 인증 | Description          |
| ------ | -------------------------------------- | ---- | -------------------- |
| `GET`  | `/public/api/news/main`                | ❌   | 메인 뉴스 목록       |
| `GET`  | `/api/news/stock`                      | ✅   | 주식 종목별 뉴스     |
| `GET`  | `/api/news/{newsId}/analysis`          | ✅   | 뉴스 AI 요약 분석    |
| `GET`  | `/api/news/{newsId}/analysis/words`    | ✅   | 뉴스 키워드 분석     |

### 학습 모듈 (`/api/studies`, `/admin/api/studies`)

| Method   | URL                              | 인증 | Description          |
| -------- | -------------------------------- | ---- | -------------------- |
| `GET`    | `/api/studies/search`            | ✅   | 학습 모듈 목록 검색  |
| `GET`    | `/api/studies/{studyId}`         | ✅   | 학습 모듈 상세       |
| `POST`   | `/api/studies/progress`          | ✅   | 학습 진도 시작       |
| `PATCH`  | `/api/studies/progress`          | ✅   | 학습 진도 업데이트   |
| `POST`   | `/admin/api/studies`             | 🔒   | 학습 모듈 등록       |
| `PUT`    | `/admin/api/studies/{studyId}`   | 🔒   | 학습 모듈 수정       |
| `DELETE` | `/admin/api/studies/{studyId}`   | 🔒   | 학습 모듈 삭제       |

### 금융 용어 사전 (`/api/words`)

| Method   | URL                                      | 인증 | Description                    |
| -------- | ---------------------------------------- | ---- | ------------------------------ |
| `GET`    | `/api/words/home`                        | ✅   | 홈 단어 (추천·최근 조회)       |
| `GET`    | `/api/words/search`                      | ✅   | 용어 검색               |
| `GET`    | `/api/words/recent-searches`             | ✅   | 최근 검색어 목록               |
| `DELETE` | `/api/words/recent-searches/{keyword}`   | ✅   | 최근 검색어 삭제               |

### 주식 (`/api/stocks`, `/public/api/stocks`)

| Method | URL                             | 인증 | Description         |
| ------ | ------------------------------- | ---- | ------------------- |
| `GET`  | `/api/stocks/{code}`            | ✅   | 주식 상세 정보      |
| `GET`  | `/api/stocks/{code}/chart`      | ✅   | 주가 차트 데이터    |
| `GET`  | `/api/stocks/{code}/analysis`   | ✅   | 주식 AI 분석        |

### 북마크 (`/api/bookmarks`)

| Method   | URL                  | 인증 | Description    |
| -------- | -------------------- | ---- | -------------- |
| `GET`    | `/api/bookmarks/my`  | ✅   | 내 북마크 목록 |
| `POST`   | `/api/bookmarks`     | ✅   | 북마크 추가    |
| `DELETE` | `/api/bookmarks`     | ✅   | 북마크 삭제    |

### 공지사항 (`/public/api/notices`, `/admin/api/notices`)

| Method   | URL                                  | 인증 | Description      |
| -------- | ------------------------------------ | ---- | ---------------- |
| `GET`    | `/public/api/notices/search`         | ❌   | 공지사항 목록    |
| `GET`    | `/public/api/notices/{noticeId}`     | ❌   | 공지사항 상세    |
| `POST`   | `/admin/api/notices`                 | 🔒   | 공지사항 등록    |
| `PUT`    | `/admin/api/notices/{noticeId}`      | 🔒   | 공지사항 수정    |
| `DELETE` | `/admin/api/notices/{noticeId}`      | 🔒   | 공지사항 삭제    |

### 금융 지표 (`/public/api/indicators`)

| Method | URL                                        | 인증 | Description    |
| ------ | ------------------------------------------ | ---- | -------------- |
| `GET`  | `/public/api/indicators/index/financial`   | ❌   | 금융 지수 조회 |
| `GET`  | `/public/api/indicators/index/market`      | ❌   | 시장 지수 조회 |

---


## 🌐 환경변수(.env) 설정

백엔드 `.env` 파일:

```dotenv
# App
ACTIVE_PROFILE=local
SERVER_PORT=8080
APP_DOMAIN=http://localhost:8080
APP_ORIGIN=http://localhost:5173

# Database
DB_URL=jdbc:mariadb://localhost:3307/finup
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# JWT
JWT_SECRET=your_jwt_secret

# AWS S3
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
S3_BUCKET_NAME=your_s3_bucket_name
FILE_DIR=/uploads
FILE_DOMAIN=http://localhost:8080

# Gmail SMTP
MAIL_USER=your_gmail@gmail.com
MAIL_PASSWORD=your_gmail_app_password

# OpenAI
OPENAI_API_KEY=your_openai_api_key

# External APIs
KIS_APP_KEY=your_kis_app_key
KIS_APP_SECRET=your_kis_app_secret
NAVER_CLIENT_ID=your_naver_client_id
NAVER_CLIENT_SECRET=your_naver_client_secret
YOUTUBE_API_KEY=your_youtube_api_key
```

프론트엔드 `.env` 파일:

```dotenv
VITE_API_BASE_URL=http://localhost:8080
```

---

## 📝 라이센스

This project is for educational purposes.

----

<div align="center">
Made with ☕ by the FinUp Team
</div>

--- 

<details>
<summary> 홈페이지 시연 GIF 미리보기 </summary>

![홈페이지 전경](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/05a52905cd2573af27ba65f37066f9ba4cb383ea/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%ED%99%88%ED%99%94%EB%A9%B4%20%ED%99%98%EC%9C%A8%20%EA%B4%80%EB%A0%A8%20%EC%A0%95%EB%B3%B4%20%ED%91%9C%EC%8B%9C.gif)

---

## 회원가입/로그인

이메일 기반 회원가입이 가능합니다.
- 회원가입 시도 시 등록한 메일로 인증코드를  전송할 수 있습니다.
- 가입 시 비밀번호 유효성 검사를 하게끔 설계했습니다.

> - 인증 메일 전송 : 스프링 부트 메일 라이브러리
- 인증 코드 검증 : 코드 발송 
            → Redis 인메모리 데이터베이스에 저장 
            → 저장된 Redis Key 기반 인증 번호 유효 및 만료여부를 판단

> 1. 인증 코드 전송 및 확인 
![인증코드](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/05a52905cd2573af27ba65f37066f9ba4cb383ea/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85%20%EC%9D%B8%EC%A6%9D%EC%BD%94%EB%93%9C.gif)
***
> 1-1. 발송된 인증코드
***
<img src="https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EC%9D%B4%EB%A9%94%EC%9D%BC%20%EC%9D%B8%EC%A6%9D%EC%BD%94%EB%93%9C%20%EC%A1%B0%ED%9A%8C.jpg"
     width="50%" height="50%"
     />
***
> 2. 비밀번호 유효성 검사 
![비밀번호 유효성 검사](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85%20%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8%20%EC%9C%A0%ED%9A%A8%EC%84%B1%20%EA%B2%80%EC%82%AC.gif)
***
> 3. 로그인
![로그인](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/927f519690d9403eca0196dde5cc57160d7e6b9d/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EB%A1%9C%EA%B7%B8%EC%9D%B8.gif)
***
> 4. 로그인 요구 기능 진입 시 반응
![로그인 요구 기능 진입](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/470e6ffefd1ad94f2b6c9af8747d1c2d46b78a1e/finup%20gif%20%EC%9E%90%EB%A3%8C/%ED%9A%8C%EC%9B%90%20%EA%B8%B0%EB%8A%A5%20%EC%A7%84%EC%9E%85%20%EC%8B%9C%20%EB%A1%9C%EA%B7%B8%EC%9D%B8%20%EC%95%88%EB%82%B4.gif)
***

## 회원 정보 관련

**개인 정보 수정**
회원은 가입 후 프로필 사진, 닉네임, 비밀번호를 수정할 수 있습니다.
- 비밀번호는 유효성 검사를 통해 올바른 규칙의 비밀번호를 입력하게끔 설정했습니다.
- 이미지 업로드를 통해 프로필 사진을 변경할 수 있고, 닉네임 역시 새로 설정 가능합니다.


>
- 프로필 변경
![프로필 사진 변경](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%ED%9A%8C%EC%9B%90%20%EC%A0%95%EB%B3%B4%20%EB%B3%80%EA%B2%BD.gif)
***
- 비밀번호 변경 시 유효성 검사
![비밀번호 변경 시 유효성 검사](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%ED%9A%8C%EC%9B%90%20%EC%A0%95%EB%B3%B4%20%EB%B3%80%EA%B2%BD.gif)


---
## 홈 페이지

### 1. 개념 학습

**수준 테스트**

현재 사용자의 금융 지식 상식을 확인해볼 수 있는 퀴즈를 풀 수 있습니다.
- 후보 단어 중 임의로 선별하여 본인의 금융 지식 수준을 판단할 수 있습니다.
- 테스트를 마친 후 수준에 맞는 콘텐츠를 학습할 수 있습니다.

> - 퀴즈 데이터 : 시사경제용어사전 용어
- 퀴즈 문제 및 보기 : 후보 중에서 AI 기반 선별 

> 1. 수준 테스트
![개념 테스트](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/927f519690d9403eca0196dde5cc57160d7e6b9d/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EA%B0%9C%EB%85%90%20%ED%85%8C%EC%8A%A4%ED%8A%B8.gif)
***
> 2. 개념 학습 (개념 정리) 페이지
![개념 학습 페이지](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/927f519690d9403eca0196dde5cc57160d7e6b9d/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EA%B0%9C%EB%85%90%20%ED%95%99%EC%8A%B5%20%EC%A7%84%EC%9E%85%20%ED%9B%84%20%ED%99%94%EB%A9%B4.gif)
***
> 3. 개념 학습 진척도 반영
![개념 학습 진척도 반영](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EA%B0%9C%EB%85%90%20%ED%95%99%EC%8A%B5%20%EC%A7%84%EC%B2%99%EB%8F%84%20%EB%B0%98%EC%98%81.gif)


### 뉴스 학습

**1. 뉴스 목록**

투자 학습에 적절한 최신 뉴스 목록을 제공합니다.

- 뉴스는 작성일 기준 최신 순으로 최대 2주까지의 기사가 제공됩니다. (무한 스크롤)
- 뉴스 검색 결과 중, 내용이 유사하거나 학습에 적절하지 않은 기사는 필터하였습니다.

> - 목록 제공 : 네이버 뉴스 API
- 필터 기준 : 기사 제목과 본문을 n-gram 토큰화 
        → jaccard, dice score 계산             
        → 일정 이상의 유사도를 보이면 필터 

***

**2. 뉴스 본문**

목록에서 원하는 뉴스를 클릭하여 뉴스 본문을 볼 수 있습니다.
뉴스를 클릭하면 다음과 같은 정보가 제공됩니다.

- 뉴스 제목, 썸네일 이미지 : 네이버 뉴스 API 제공
- 언론사, 뉴스 본문 : 네이버 뉴스 API 에서는 뉴스 본문과 언론사 정보 미제공
               → API 제공 뉴스링크 기반 원본 기사에서 크롤링 후 제공
- AI 분석 : 현재 뉴스내용에 맞는 초보자에게 유용한 기사 분석 제공
- AI 키워드 : 기사와 관련 있는 추천 경제 용어 제공 (RAG 추천 기반 시사경제용어사전 용어 중 선별하여 제공)

***

>
1. 뉴스 키워드 재추천 및 단어 바로가기
![뉴스 단어 재추천](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/1bae16a536e5aa2296eeff32db3ce0a372c6314e/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EB%89%B4%EC%8A%A4%20AI%20%EB%89%B4%EC%8A%A4%20%ED%82%A4%EC%9B%8C%EB%93%9C%20%EC%B6%94%EC%B2%9C.gif)
***
2. 뉴스 원문 바로가기
![뉴스 원문 바로가기](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/927f519690d9403eca0196dde5cc57160d7e6b9d/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EB%89%B4%EC%8A%A4%20%EC%9B%90%EB%AC%B8%20%EB%B0%94%EB%A1%9C%EA%B0%80%EA%B8%B0.gif)
***
3. 뉴스 살펴보기
![뉴스 살펴보기](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/927f519690d9403eca0196dde5cc57160d7e6b9d/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EB%89%B4%EC%8A%A4%20AI%20%EB%B6%84%EC%84%9D%20%EA%B8%B0%EB%8A%A5.gif)
***
4. 뉴스 무한 스크롤 구현
![뉴스 무한 스크롤 구현](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EB%89%B4%EC%8A%A4%20%EB%AC%B4%ED%95%9C%20%EC%8A%A4%ED%81%AC%EB%A1%A4%20%EA%B5%AC%ED%98%84.gif)

* * *
### 종목 학습

**1. 종목 목록**

실제 거래되는 국내 주식 종목 기반 투자 학습 정보를 제공합니다.

- 정보 제공 : KIS(한국투자증권) API
- 제공 목록 : 시가총액 순, 거래대금 순으로 각각 상위 30개 종목
***

>
- 종목 학습 탭, 시가총액 거래대금 순위 확인
![종목 학습 탭](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EC%A2%85%EB%AA%A9%20%ED%99%94%EB%A9%B4(%EC%8B%9C%EA%B0%80%EC%B4%9D%EC%95%A1%20%EB%93%B1%20%ED%99%95%EC%9D%B8).gif)

***

**2-1. 차트**
현재 종목에 대한 100개의 캔들로 이루어진 차트를 제공합니다.
시가, 고가, 저가, 종가, 거래량, 5일/20일 평균선 정보를 제공합니다.

- 제공 차트 : 일봉, 주봉, 월봉
- AI 분석 : 현재 차트 기반 종합적, 추세, 리스크, 거래량 분석 정보

>
![종목별 차트 확인](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/1bae16a536e5aa2296eeff32db3ce0a372c6314e/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EC%A2%85%EB%AA%A9%20%EC%B0%A8%ED%8A%B8%20%ED%99%95%EC%9D%B8.gif)

***

**2-2. 상세**
현재 종목의 상세 지표 및 분석 정보를 확인할 수 있습니다.
- 지표 설명
- 여러 관점에서의 AI 분석
- 추천 영상 : 종목명으로 유튜브 검색 API > 검색된 영상 후보를 AI 선별 후 제공하는 RAG 추천으로 제공

>![종목별 상세 항목 확인](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EC%A2%85%EB%AA%A9%EB%B3%84%20%EC%83%81%EC%84%B8%20%ED%95%AD%EB%AA%A9%20%ED%99%95%EC%9D%B8.gif)


***
**2-3. 뉴스**
- 현재 종목에 특화된 종목과 연계하여 학습하기 적절한 뉴스 목록을 제공합니다.
제공 방식은 상단의 "뉴스 본문" 과 동일합니다.

>![종목별 뉴스](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/1bae16a536e5aa2296eeff32db3ce0a372c6314e/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EC%A2%85%EB%AA%A9%EB%B3%84%20%EB%89%B4%EC%8A%A42.gif)

---
### 단어 학습 (개념 테스트 전용 단어들)

학습자가 궁금한 경제 용어를 검색하고 학습할 수 있습니다.
제공되는 단어 기반은 모두 시사경제용어사전 용어를 이용했습니다.

***
**1. 단어장 홈**
단어 학습 전반에 도움이 될 기능을 제공합니다.
구체적으론 아래 기능들이 제공됩니다.

1. 과거 검색어 목록
	- Redis List 자료형에 저장하여, 최근 20개의 검색어 목록을 빠르게 제공
2. 오늘의 단어 및 퀴즈
3. 단어 검색 바
***

**2. 단어 검색**
검색한 단어 키워드와 연관성이 높은 검색 결과를 제공합니다. (벡터 유사도 기반)
최대 20개의 검색 결과를 제공합니다.

***
**3. 단어 상세**
검색된 단어를 클릭하면, 단어 상세 뜻 확인 및 단어장 저장 기능이 제공됩니다.
"뉴스 상세" 에서 제공되는 뉴스 키워드를 클릭 시에도 확인 가능합니다.

>
- 단어장 퀴즈 기능
![단어장 퀴즈 기능](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EB%8B%A8%EC%96%B4%EC%9E%A5%20%EB%8B%A8%EC%96%B4%ED%80%B4%EC%A6%88.gif)
***
>
- 내 단어장 기능
![내 단어장 기능](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EB%82%B4%20%EB%8B%A8%EC%96%B4%EC%9E%A5%20%EA%B8%B0%EB%8A%A5.gif)
***
>
- 내 단어장에 단어 저장하기
![내 단어장에 단어 저장](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EB%82%B4%20%EB%8B%A8%EC%96%B4%EC%9E%A5%EC%97%90%20%EB%8B%A8%EC%96%B4%20%EC%A0%80%EC%9E%A5.gif)
***
>
- 단어 검색 기능 (벡터 연산을 통한 상위 20개 단어 출력 및 최근 검색어 저장)
![단어 검색 기능](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EB%8B%A8%EC%96%B4%20%EA%B2%80%EC%83%89%20%EA%B8%B0%EB%8A%A5.gif)
***
>
- 기획재정부 시사경제용어사전 링크 (추가 학습에 용이하도록 설계)
![기획재정부 시사경제용어사전 링크](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EA%B8%B0%EC%9E%AC%EB%B6%80%20%EC%8B%9C%EC%82%AC%EA%B2%BD%EC%A0%9C%EC%9A%A9%EC%96%B4%EC%82%AC%EC%A0%84.gif)

---
### 관리자 기능

#### 회원 목록

관리자 화면에서 회원 목록을 별도로 조회할 수 있는 화면입니다.
- 개인정보 보호를 위한 마스킹 기능을 적용했습니다.
- pdf, xlsx 확장자 형식의 회원 전체 목록을 다운로드 받을 수 있습니다.

>
- 회원 목록 일부 글자 마스킹 기능
![회원 목록 일부 글자 마스킹 기능](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/cf1c0237f8ce04541a51602c21c34d0f09d598d2/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EA%B4%80%EB%A6%AC%EC%9E%90%20%ED%9A%8C%EC%9B%90%20%EB%AA%A9%EB%A1%9D%20%EA%B4%80%EB%A6%AC%20%EB%A7%88%EC%8A%A4%ED%82%B9.gif)
***
- 회원 목록 pdf, xlsx 파일 다운로드
![회원 목록 pdf, xlsx 파일 다운로드](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%ED%9A%8C%EC%9B%90%20%EB%AA%A9%EB%A1%9D%20pdf%2C%20xlsx%20%ED%8C%8C%EC%9D%BC%20%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C.gif)

#### 공지사항
홈화면에서 사용자에게 최근 3개의 공지사항을 표시합니다.
- 관리자는 별도로 공지사항을 등록/수정할 수 있습니다.
- 관리자 메뉴에서 공지사항 게시글 검색이 가능하며, 조회가 가능합니다.

>
- 공지사항 검색
![공지사항 검색](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD%20%EA%B2%80%EC%83%89.gif)
***
- 공지사항 등록
![공지사항 등록](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/cf1c0237f8ce04541a51602c21c34d0f09d598d2/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD%20%EB%93%B1%EB%A1%9D.gif)


#### 개념 학습 관리

개념 학습에서 회원이 학습할 수 있는 파트를 관리하는 화면입니다.
- 관리자가 별도로 개념 학습 파트 조회 및 수정이 가능합니다.
- 마찬가지로 규칙에 따른 유효성 검사 기능이 있습니다.

>
- 항목 조회 및 편집 기능
![개념 학습 조회 및 편집](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EA%B0%9C%EB%85%90%20%ED%95%99%EC%8A%B5%20%EA%B4%80%EB%A6%AC%20-%20%ED%8E%B8%EC%A7%91%20%EC%A1%B0%ED%9A%8C.gif)


#### 단어 관리 (개념 학습 파트, 관리자 전용)
개념 학습에서 별도로 사용되는 단어를 등록할 수 있습니다.
- 마찬가지로 단어 규칙에 따라 유효성 검사를 통해 수정, 등록이 가능하게끔 했습니다.
- 이미지 썸네일을 등록할 수 있습니다.
- 등록 시간순 등 3가지 조건을 통해 단어 정렬이 가능합니다.

>
- 단어 조회 및 등록 유효성 검사
![단어 조회 및 등록 유효성 검사](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EB%8B%A8%EC%96%B4%20%EC%A1%B0%ED%9A%8C%2C%20%EB%93%B1%EB%A1%9D%20%EC%9C%A0%ED%9A%A8%EC%84%B1%20%EA%B2%80%EC%82%AC.gif)
***
- 단어 정렬 기능
![단어 정렬 기능](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EB%8B%A8%EC%96%B4%20%EC%A0%95%EB%A0%AC%20%EA%B8%B0%EB%8A%A5.gif)

#### 유튜브 영상 관리(개념 학습 파트)
개념 학습 파트에서 볼 수 있는 추천 영상들을 등록할 수 있습니다.
- 유튜브 영상 목록 정렬 기능이 제공됩니다.
- 관리자 화면 내에서 등록된 유튜브 영상 중 별도 검색이 가능합니다.
- 영상 등록 시 올바른 링크가 등록되도록 유효성 검사 조건을 설계했습니다.

>
- 유튜브 영상 목록 정렬 기능
![유튜브 영상 목록 정렬 기능](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EC%9C%A0%ED%8A%9C%EB%B8%8C%20%EC%98%81%EC%83%81%20%EC%A0%95%EB%A0%AC%20%EA%B8%B0%EB%8A%A5.gif)
***
- 유튜브 영상 검색 기능
![유튜브 영상 검색 기능](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EC%9C%A0%ED%8A%9C%EB%B8%8C%20%EC%98%81%EC%83%81%20%EA%B2%80%EC%83%89%20%EA%B8%B0%EB%8A%A5.gif)
***
- 유튜브 영상 등록 및 유효성 검사, 삭제
![유튜브 영상 등록 및 유효성 검사, 삭제](https://raw.githubusercontent.com/DarkLight0418/finup-markdown-test/2ec7664ef0a63bbc4a802cb8481267df809b339a/gif%20%EC%9E%90%EB%A3%8C/gif/%EC%9C%A0%ED%8A%9C%EB%B8%8C%20%EB%8B%A8%EC%96%B4%20%EB%93%B1%EB%A1%9D.gif)

***
## 그 외
#### AI 항목 재추천 기능
AI 분석, 추천 항목들은 재추천 버튼을 클릭하면 다시 AI 분석을 통한 재추천 조회 결과가 제공됩니다.

> - 뉴스 해설 AI 재추천 등
![뉴스 해설 AI 재추천](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/1bae16a536e5aa2296eeff32db3ce0a372c6314e/finup%20gif%20%EC%9E%90%EB%A3%8C/gif/%EC%A2%85%EB%AA%A9%20%EB%89%B4%EC%8A%A4%20%EC%9E%AC%EC%B6%94%EC%B2%9C.gif)
***
- AI 뉴스 키워드 재추천 등
![AI 뉴스 키워드 재추천](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/c87f72dfa9f0863d195b62cf047460ea06d523a2/finup%20gif%20%EC%9E%90%EB%A3%8C/%EB%89%B4%EC%8A%A4%20AI%20%ED%82%A4%EC%9B%8C%EB%93%9C%20%EC%B6%94%EC%B2%9C.gif)
***
- 종목 AI 분석 재분석 버튼 클릭 시 갱신
![종목 AI 분석 재분석 버튼 클릭 시 갱신](https://raw.githubusercontent.com/DarkLight0418/Project_GIF_Files/470e6ffefd1ad94f2b6c9af8747d1c2d46b78a1e/finup%20gif%20%EC%9E%90%EB%A3%8C/%EC%A2%85%EB%AA%A9%20%EB%B6%84%EC%84%9D%20AI%20%EB%B6%84%EC%84%9D%20%EC%9E%AC%EC%B6%94%EC%B2%9C.gif)

</details>
