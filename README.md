# Board Project Demo

**Last Updated:** 2026.01.12

## 1. 프로젝트 소개

Spring Boot와 Spring Security를 활용한 **하이브리드 게시판 애플리케이션**입니다. 전통적인 MVC 웹 인터페이스와 최신 REST API를 모두 제공하여, 웹 브라우저와 모바일 앱 등 다양한 클라이언트를 지원합니다.

## 2. 주요 기능

### 웹 애플리케이션 (MVC)
- 회원가입 및 로그인 (BCrypt 암호화)
- Spring Security 기반 인증/인가
- 게시글 CRUD (작성자 권한 검증)
- 페이징 처리된 목록 조회
- 댓글 시스템 (작성/수정/삭제) 및 조회수 증가
- 개인정보 마스킹 처리

### REST API
- **JWT 토큰 기반 인증** - Stateless 인증 방식
- **완전한 RESTful API** - 13개 엔드포인트 제공
- **좋아요 소셜 기능** - 실시간 좋아요/취소 시스템
- **자동 API 문서화** - Swagger/OpenAPI 3.0
- **계층적 권한 체계** - GUEST/ADMIN 구분
- **표준화된 JSON 응답** - 일관된 응답 형식
- **모바일/SPA 지원** - 다양한 클라이언트 연동 가능

### JavaScript 클라이언트 (SPA)
- **완전한 프론트엔드 구현** - 모든 REST API 연동 완료
- **JWT 토큰 관리** - 자동 인증 헤더 처리
- **모듈화된 아키텍처** - 9개 JavaScript 모듈
- **낙관적 업데이트** - 즉각적인 사용자 경험
- **반응형 UI** - 모바일 친화적 디자인
- **실시간 상태 관리** - StateManager 기반

## 3. 기술 스택

- **Backend**: Spring Boot 3.4.11, Java 17
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA, Hibernate
- **Security**: Spring Security 6 + JWT
- **Template Engine**: Thymeleaf
- **API Documentation**: SpringDoc OpenAPI 3.0 (Swagger)
- **Build Tool**: Maven
- **기타**: Lombok, Spring Boot DevTools

## 4. 빠른 시작

### 사전 요구사항
- Java 17 이상
- Maven 3.6 이상
- MySQL 8.0 이상

### 설치 및 실행
```bash
# 1. 프로젝트 클론
git clone <repository-url>
cd board-project-demo

# 2. 데이터베이스 생성
mysql -u root -p
CREATE DATABASE demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 데이터베이스 연결 정보 수정
# src/main/resources/application.yml 파일 편집

# 4. 애플리케이션 실행
./mvnw spring-boot:run
```

### 접속 정보
- **웹 애플리케이션**: http://localhost:8080
- **JavaScript 클라이언트**: http://localhost:8000 (Python 웹서버 실행 시)
- **REST API 문서**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 5. 프로젝트 구조

```
board-project-demo/
├── src/main/java/com/example/demo/
│   ├── BoardProjectDemoApplication.java    # 메인 애플리케이션 클래스
│   ├── config/
│   │   ├── SecurityConfig.java             # MVC 보안 설정
│   │   ├── RestSecurityConfig.java         # REST API 보안 설정
│   │   ├── SwaggerConfig.java              # Swagger 설정
│   │   ├── JwtConfig.java                  # JWT 설정
│   │   ├── CorsConfig.java                 # CORS 설정
│   │   └── CacheConfig.java                # 캐싱 설정
│   ├── controller/                         # MVC 웹 요청 처리
│   │   ├── BoardController.java
│   │   ├── CommentController.java
│   │   ├── UserController.java
│   │   ├── AuthRestController.java         # REST 인증 API
│   │   ├── UserRestController.java         # REST 사용자 API
│   │   ├── BoardRestController.java        # REST 게시판 API
│   │   ├── CommentRestController.java      # REST 댓글 API
│   │   └── BoardLikeRestController.java    # REST 좋아요 API
│   ├── dto/                               # 데이터 전송 객체
│   │   ├── BoardDetailResponseDto.java     # MVC용 DTO
│   │   ├── BoardListResponseDto.java
│   │   ├── CommentResponseDto.java
│   │   ├── ApiResponseDto.java             # REST API 공통 응답
│   │   ├── LoginRequestDto.java            # REST API 요청 DTO
│   │   ├── LoginResponseDto.java           # REST API 응답 DTO
│   │   ├── SignupRequestDto.java
│   │   ├── BoardCreateRequestDto.java
│   │   ├── BoardUpdateRequestDto.java
│   │   ├── CommentCreateRequestDto.java
│   │   ├── BoardLikeToggleDto.java         # 좋아요 토글 응답 DTO
│   │   └── BoardLikeStatusDto.java         # 좋아요 상태 조회 DTO
│   ├── entity/                            # JPA 엔티티
│   │   ├── BaseEntity.java
│   │   ├── Board.java
│   │   ├── Comment.java
│   │   ├── User.java
│   │   ├── Role.java
│   │   └── BoardLike.java                  # 좋아요 엔티티
│   ├── repository/                        # 데이터 접근 계층
│   │   ├── BoardRepository.java
│   │   ├── CommentRepository.java
│   │   ├── UserRepository.java
│   │   └── BoardLikeRepository.java        # 좋아요 데이터 접근
│   ├── service/                           # 비즈니스 로직
│   │   ├── BoardService.java
│   │   ├── CommentService.java
│   │   ├── UserService.java
│   │   └── BoardLikeService.java           # 좋아요 비즈니스 로직
│   ├── security/                          # 보안 관련
│   │   └── UserSecurityService.java
│   ├── jwt/                               # JWT 관련
│   │   ├── JwtTokenProvider.java
│   │   └── JwtAuthenticationFilter.java
│   ├── exception/                         # 예외 처리
│   │   ├── UnauthorizedAccessException.java
│   │   └── RestExceptionHandler.java      # REST API 예외 처리
│   └── util/                             # 유틸리티
│       ├── MaskingUtil.java
│       └── CacheUtil.java
├── src/main/resources/
│   ├── application.yml                    # 애플리케이션 설정
│   ├── static/                           # 정적 리소스
│   └── templates/                        # Thymeleaf 템플릿
│       ├── index.html
│       ├── login_form.html
│       ├── signup_form.html
│       └── board/
├── client/                               # JavaScript SPA 클라이언트
│   ├── js/
│   │   ├── auth.js                       # JWT 인증 관리
│   │   ├── api.js                        # REST API 호출
│   │   ├── board-service.js              # 게시판 서비스
│   │   ├── user-service.js               # 사용자 서비스
│   │   ├── like-service.js               # 좋아요 서비스
│   │   ├── state.js                      # 상태 관리
│   │   ├── GlobalErrorHandler.js         # 통합 오류 처리 시스템
│   │   ├── page-init.js                  # 페이지 공통 초기화
│   │   └── app.js                        # 메인 애플리케이션
│   ├── css/
│   │   ├── style.css                     # 메인 스타일시트
│   │   ├── common.css                    # 공통 스타일
│   │   ├── components.css                # 컴포넌트 스타일
│   │   ├── board.css                     # 게시판 스타일 (좋아요 포함)
│   │   ├── auth.css                      # 인증 스타일
│   │   └── admin.css                     # 관리자 스타일
│   ├── login.html                        # 로그인 페이지
│   ├── signup.html                       # 회원가입 페이지
│   ├── boards.html                       # 게시글 목록
│   ├── board-create.html                 # 게시글 작성
│   ├── board-detail.html                 # 게시글 상세 (좋아요 포함)
│   ├── board-edit.html                   # 게시글 수정
│   ├── admin.html                        # 관리자 페이지
│   └── README.md                         # 클라이언트 사용 가이드
├── REST_API_개발계획서.md                 # REST API 개발 계획
├── REST_API_구현진행상황.md               # 구현 진행 현황
├── REST_API_사용가이드.md                 # API 사용 가이드
└── pom.xml                               # Maven 설정
```

## 6. 아키텍처 개요

### 레이어드 아키텍처
본 프로젝트는 계층형 아키텍처 패턴을 따라 각 계층의 책임을 명확히 분리했습니다.

#### Presentation Layer (Controller)
- **역할**: HTTP 요청/응답 처리, 사용자 입력 검증
- **구성요소**: `@Controller` 어노테이션이 붙은 클래스들
- **특징**: 
  - RESTful하지 않은 전통적인 MVC 패턴 사용
  - Thymeleaf를 통한 서버사이드 렌더링
  - Spring Security와 연동된 인증/인가 처리

#### Business Layer (Service)
- **역할**: 핵심 비즈니스 로직 처리, 트랜잭션 관리
- **구성요소**: `@Service` 어노테이션이 붙은 클래스들
- **특징**:
  - `@Transactional` 어노테이션을 통한 선언적 트랜잭션 관리
  - 도메인 규칙 및 비즈니스 제약사항 구현
  - DTO 변환 로직 포함

#### Data Access Layer (Repository)
- **역할**: 데이터베이스 접근 및 영속성 관리
- **구성요소**: Spring Data JPA Repository 인터페이스들
- **특징**:
  - JPA/Hibernate를 통한 ORM 매핑
  - 쿼리 메서드 자동 생성
  - 페이징 및 정렬 기능 내장

#### Domain Layer (Entity)
- **역할**: 핵심 도메인 모델 정의
- **구성요소**: JPA Entity 클래스들
- **특징**:
  - JPA Auditing을 통한 생성/수정 시간 자동 관리
  - 연관관계 매핑을 통한 객체 그래프 구성
  - Lombok을 활용한 보일러플레이트 코드 제거

### 주요 디자인 패턴

#### DTO Pattern
- **목적**: 계층 간 데이터 전송 시 캡슐화 및 성능 최적화
- **구현**: Response DTO를 통한 필요한 데이터만 전송
- **장점**: 엔티티 직접 노출 방지, 네트워크 트래픽 최적화

#### Repository Pattern
- **목적**: 데이터 접근 로직의 캡슐화 및 테스트 용이성 향상
- **구현**: Spring Data JPA의 Repository 인터페이스 활용
- **장점**: 데이터베이스 독립성, 쿼리 로직 중앙화

#### Template Method Pattern
- **목적**: 공통 기능의 재사용성 향상
- **구현**: BaseEntity를 통한 공통 필드 상속
- **장점**: 코드 중복 제거, 일관된 데이터 관리

### 보안 아키텍처

#### Spring Security Filter Chain
```
HTTP Request → SecurityFilterChain → Authentication → Authorization → Controller
```

**MVC 보안 (세션 기반):**
- **인증 필터**: 사용자 자격 증명 검증
- **인가 필터**: URL 패턴별 접근 권한 확인
- **CSRF 필터**: Cross-Site Request Forgery 공격 방지
- **세션 관리**: 세션 기반 상태 유지

**REST API 보안 (JWT 기반):**
- **JWT 인증 필터**: JWT 토큰 검증 및 사용자 인증
- **Stateless 인증**: 서버에 세션 상태 저장하지 않음
- **Bearer 토큰**: Authorization 헤더를 통한 토큰 전송
- **토큰 만료 관리**: Access Token (1시간) + Refresh Token (7일)

#### 보안 구성요소
- **PasswordEncoder**: BCrypt 알고리즘을 통한 단방향 암호화
- **UserDetailsService**: 사용자 정보 로딩 및 권한 부여
- **SecurityContext**: 인증된 사용자 정보 저장 및 관리
- **JwtTokenProvider**: JWT 토큰 생성, 검증 및 사용자 정보 추출

### 데이터 플로우

#### 게시글 조회 플로우
```
Client Request → BoardController → BoardService → BoardRepository → Database
                     ↓
Client Response ← Thymeleaf ← DTO ← Entity ← JPA/Hibernate
```

#### 트랜잭션 관리
- **선언적 트랜잭션**: `@Transactional` 어노테이션 사용
- **전파 속성**: 기본 REQUIRED 전파 레벨 사용
- **롤백 정책**: RuntimeException 발생 시 자동 롤백

## 7. API 엔드포인트

### 웹 애플리케이션 (MVC)

#### 사용자 관리
| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| GET | `/user/signup` | 회원가입 폼 | X |
| POST | `/user/signup` | 회원가입 처리 | X |
| GET | `/user/login` | 로그인 폼 | X |
| POST | `/user/login` | 로그인 처리 | X |
| POST | `/user/logout` | 로그아웃 | O |

#### 게시판 관리
| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| GET | `/board/list` | 게시글 목록 (페이징) | X |
| GET | `/board/detail/{id}` | 게시글 상세 조회 | X |
| GET | `/board/create` | 게시글 작성 폼 | O |
| POST | `/board/create` | 게시글 작성 처리 | O |
| GET | `/board/modify/{id}` | 게시글 수정 폼 | O |
| POST | `/board/modify/{id}` | 게시글 수정 처리 | O |
| POST | `/board/delete/{id}` | 게시글 삭제 | O |

#### 댓글 관리
| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| POST | `/comment/create` | 댓글 작성 | O |
| POST | `/comment/delete/{id}` | 댓글 삭제 | O |

### REST API

#### 인증 API
| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| POST | `/api/v1/auth/signup` | 회원가입 | X |
| POST | `/api/v1/auth/login` | 로그인 (JWT 토큰 발급) | X |

#### 사용자 관리 API
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | `/api/v1/users` | 회원 목록 조회 | ADMIN |
| GET | `/api/v1/users/me` | 내 정보 조회 | USER |
| PUT | `/api/v1/users/{userNo}/role` | 권한 변경 | ADMIN |

#### 게시판 API
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | `/api/v1/boards` | 게시글 목록 (페이징) | ALL |
| GET | `/api/v1/boards/{boardNo}` | 게시글 상세 조회 | ALL |
| POST | `/api/v1/boards` | 게시글 작성 | USER |
| PUT | `/api/v1/boards/{boardNo}` | 게시글 수정 | OWNER만 |
| DELETE | `/api/v1/boards/{boardNo}` | 게시글 삭제 | OWNER/ADMIN |

#### 댓글 API
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | `/api/v1/boards/{boardNo}/comments` | 댓글 목록 | ALL |
| POST | `/api/v1/boards/{boardNo}/comments` | 댓글 작성 | USER |
| PUT | `/api/v1/boards/{boardNo}/comments/{commentNo}` | 댓글 수정 | OWNER만 |
| DELETE | `/api/v1/boards/{boardNo}/comments/{commentNo}` | 댓글 삭제 | OWNER/ADMIN |

#### 좋아요 API
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | `/api/v1/boards/{boardNo}/like` | 좋아요 상태 조회 | USER |
| POST | `/api/v1/boards/{boardNo}/like` | 좋아요 토글 (추가/취소) | USER |

### 요청/응답 예시

#### 웹 애플리케이션
```
GET /board/list?page=0&size=10

Response: HTML (Thymeleaf 렌더링)
- 페이징된 게시글 목록
- 페이지네이션 UI
```

#### REST API
```bash
# 로그인
POST /api/v1/auth/login
Content-Type: application/json

{
  "userId": "testuser01",
  "password": "password123"
}

# 응답
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userNo": 1,
      "userId": "testuser01",
      "role": "GUEST"
    }
  }
}

# 게시글 작성
POST /api/v1/boards
Authorization: Bearer {토큰}
Content-Type: application/json

{
  "title": "게시글 제목",
  "content": "게시글 내용입니다."
}

# 댓글 수정
PUT /api/v1/boards/{boardNo}/comments/{commentNo}
Authorization: Bearer {토큰}
Content-Type: application/json

{
  "content": "수정된 댓글 내용입니다."
}

# 좋아요 토글
POST /api/v1/boards/{boardNo}/like
Authorization: Bearer {토큰}

# 응답
{
  "success": true,
  "data": {
    "liked": true,
    "likeCount": 16
  }
}
```

## 🎯 프로젝트 완성도

### ✅ 완료된 기능 (100%)

**백엔드 (Spring Boot)**
- MVC 웹 애플리케이션 완전 구현
- REST API 13개 엔드포인트 완전 구현
- JWT 인증 시스템 완료
- Spring Security 이중 보안 설정 완료
- Swagger API 문서화 완료
- 댓글 수정 기능 완료 (2025.12.30)
- 탈퇴 회원 마스킹 예외 처리 완료 (2025.12.30)
- 에러 처리 시스템 통합 완료 (2026.01.08)
- 관리자 페이지 기본 구현 완료 (2026.01.08)
- 좋아요 소셜 기능 완료 (2026.01.12)

**프론트엔드 (JavaScript SPA)**
- 완전한 SPA 클라이언트 구현
- 모든 REST API 연동 완료
- JWT 토큰 관리 시스템 완료
- 댓글 CRUD 기능 완료 (수정 포함)
- 반응형 UI 구현
- GlobalErrorHandler 통합 에러 처리 시스템 완료 (2026.01.08)
- 코드 모듈화 및 구조 개선 완료 (2026.01.08)
- 좋아요 낙관적 업데이트 시스템 완료 (2026.01.12)

**보안 및 성능**
- BCrypt 암호화
- CORS 설정
- 캐싱 시스템
- 권한 기반 접근 제어
- 개인정보 마스킹 (탈퇴 회원 예외 처리)

---

## 부록

### A. 데이터베이스 설계

#### 테이블 구조
```sql
-- 사용자 테이블
TB_USER (
    user_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    create_dt DATETIME,
    update_dt DATETIME
)

-- 게시글 테이블
TB_BOARD (
    board_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(300) NOT NULL,
    content TEXT NOT NULL,
    view_cnt INT DEFAULT 0,
    author_no BIGINT NOT NULL,
    create_dt DATETIME,
    update_dt DATETIME
)

-- 댓글 테이블
TB_COMMENT (
    comment_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    board_no BIGINT NOT NULL,
    author_no BIGINT NOT NULL,
    create_dt DATETIME,
    update_dt DATETIME
)
-- 좋아요 테이블
TB_BOARD_LIKE (
    like_no BIGINT PRIMARY KEY AUTO_INCREMENT,
    board_no BIGINT NOT NULL,
    user_no BIGINT NOT NULL,
    create_dt DATETIME,
    update_dt DATETIME,
    UNIQUE KEY uk_board_user_like (board_no, user_no)
)
```

#### 연관관계
- User : Board = 1 : N (한 사용자는 여러 게시글 작성 가능)
- User : Comment = 1 : N (한 사용자는 여러 댓글 작성 가능)
- Board : Comment = 1 : N (한 게시글에 여러 댓글 작성 가능)
- User : BoardLike = 1 : N (한 사용자는 여러 게시글에 좋아요 가능)
- Board : BoardLike = 1 : N (한 게시글은 여러 사용자로부터 좋아요 받을 수 있음)

### B. 개발 환경 설정

#### IDE 설정
- **Eclipse**: Spring Tools 4 플러그인 설치 권장
- **IntelliJ IDEA**: Spring Boot 플러그인 활성화
- **Lombok**: IDE에 Lombok 플러그인 설치 필수

#### 개발 도구
- **Spring Boot DevTools**: 자동 재시작 및 LiveReload
- **JPA DDL**: `hibernate.ddl-auto=update` (개발 환경)
- **SQL 로깅**: `show-sql=true`, `format_sql=true`

### C. 향후 개선 사항

#### 기능 확장
- 파일 업로드 기능 (이미지, 첨부파일)
- 게시글 검색 기능 (제목, 내용, 작성자)
- 게시글 카테고리 분류
- 좋아요 고급 기능 (좋아요 목록, 인기 게시글)
- 실시간 알림 시스템

#### 성능 최적화
- Redis 캐싱 적용
- 데이터베이스 인덱스 최적화
- 이미지 CDN 연동
- 페이지 로딩 속도 개선

#### 보안 강화
- OAuth2 소셜 로그인
- API Rate Limiting
- XSS 방지 강화
