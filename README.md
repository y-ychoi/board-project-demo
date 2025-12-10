# Board Project Demo

Spring Boot를 사용한 게시판 웹 애플리케이션입니다.

## 기술 스택

- **Backend**: Spring Boot 3.4.11, Java 17
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA, Hibernate
- **Security**: Spring Security 6
- **Template Engine**: Thymeleaf + Thymeleaf Spring Security 6
- **Build Tool**: Maven
- **기타**: Lombok, Spring Boot DevTools

## 주요 기능

### 사용자 관리
- 회원가입 및 로그인 (BCrypt 암호화)
- Spring Security를 통한 인증/인가
- 사용자별 게시글 작성 권한 관리
- 개인정보 마스킹 처리 (MaskingUtil)

### 게시판 기능
- 게시글 목록 조회 (페이징 처리)
- 게시글 작성, 수정, 삭제 (작성자 권한 검증)
- 게시글 상세 조회 및 조회수 증가
- 작성자 정보 표시

### 댓글 시스템
- 게시글별 댓글 작성
- 댓글 목록 조회
- 댓글 삭제 기능 (작성자 권한 검증)

## 프로젝트 구조

```
board-project-demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── BoardProjectDemoApplication.java    # 메인 애플리케이션 클래스 (@EnableJpaAuditing)
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java             # Spring Security 설정
│   │   │   ├── controller/                         # 컨트롤러 레이어
│   │   │   │   ├── BoardController.java           # 게시판 컨트롤러 (CRUD, 페이징)
│   │   │   │   ├── CommentController.java         # 댓글 컨트롤러 (작성, 삭제)
│   │   │   │   └── UserController.java            # 사용자 컨트롤러 (회원가입, 로그인)
│   │   │   ├── dto/                               # 데이터 전송 객체
│   │   │   │   ├── BoardDetailResponseDto.java    # 게시글 상세 응답 DTO
│   │   │   │   ├── BoardListResponseDto.java      # 게시글 목록 응답 DTO
│   │   │   │   └── CommentResponseDto.java        # 댓글 응답 DTO
│   │   │   ├── entity/                            # JPA 엔티티
│   │   │   │   ├── BaseEntity.java               # 공통 엔티티 (생성일, 수정일 - JPA Auditing)
│   │   │   │   ├── Board.java                    # 게시글 엔티티 (TB_BOARD)
│   │   │   │   ├── Comment.java                  # 댓글 엔티티 (TB_COMMENT)
│   │   │   │   └── User.java                     # 사용자 엔티티 (TB_USER)
│   │   │   ├── exception/                         # 예외 처리
│   │   │   │   └── UnauthorizedAccessException.java # 권한 없음 예외
│   │   │   ├── repository/                        # 데이터 접근 레이어
│   │   │   │   ├── BoardRepository.java          # 게시판 리포지토리
│   │   │   │   ├── CommentRepository.java        # 댓글 리포지토리
│   │   │   │   └── UserRepository.java           # 사용자 리포지토리
│   │   │   ├── security/                          # Spring Security 설정
│   │   │   │   └── UserSecurityService.java      # 사용자 인증 서비스
│   │   │   ├── service/                           # 비즈니스 로직 레이어
│   │   │   │   ├── BoardService.java             # 게시판 서비스 (@Transactional)
│   │   │   │   ├── CommentService.java           # 댓글 서비스
│   │   │   │   └── UserService.java              # 사용자 서비스
│   │   │   └── util/                             # 유틸리티 클래스
│   │   │       └── MaskingUtil.java              # 개인정보 마스킹 유틸
│   │   └── resources/
│   │       ├── application.yml                    # 애플리케이션 설정
│   │       ├── static/                           # 정적 리소스 (CSS, JS, 이미지)
│   │       └── templates/                        # Thymeleaf 템플릿
│   │           ├── index.html                    # 메인 페이지
│   │           ├── login_form.html               # 로그인 폼
│   │           ├── signup_form.html              # 회원가입 폼
│   │           └── board/                        # 게시판 관련 템플릿
│   │               ├── create_form.html          # 게시글 작성 폼
│   │               ├── detail.html               # 게시글 상세 페이지
│   │               ├── list.html                 # 게시글 목록 페이지
│   │               └── modify_form.html          # 게시글 수정 폼
│   └── test/
│       └── java/com/example/demo/
│           └── BoardProjectDemoApplicationTests.java # 기본 테스트 클래스
├── pom.xml                                        # Maven 의존성 설정
├── mvnw                                          # Maven Wrapper (Unix)
├── mvnw.cmd                                      # Maven Wrapper (Windows)
└── README.md                                     # 프로젝트 문서
```

## 아키텍처 분석

### 레이어드 아키텍처
- **Presentation Layer**: Controller (사용자 요청 처리)
- **Business Layer**: Service (비즈니스 로직 처리)
- **Data Access Layer**: Repository (데이터베이스 접근)
- **Domain Layer**: Entity (도메인 모델)

### 주요 디자인 패턴
- **DTO Pattern**: 계층 간 데이터 전송을 위한 객체 분리
- **Repository Pattern**: 데이터 접근 로직 캡슐화
- **Builder Pattern**: 엔티티 객체 생성 (Lombok @Builder)
- **Template Method Pattern**: BaseEntity를 통한 공통 필드 상속

### 보안 아키텍처
- **Spring Security Filter Chain**: 요청 인증/인가 처리
- **BCrypt Password Encoding**: 비밀번호 암호화
- **CSRF Protection**: Cross-Site Request Forgery 방지
- **Session-based Authentication**: 세션 기반 인증

## 데이터베이스 설계

### 테이블 구조
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
```

### 연관관계
- User : Board = 1 : N (한 사용자는 여러 게시글 작성 가능)
- User : Comment = 1 : N (한 사용자는 여러 댓글 작성 가능)
- Board : Comment = 1 : N (한 게시글에 여러 댓글 작성 가능)

## 주요 기능 상세

### JPA Auditing
- `@EnableJpaAuditing`: 메인 클래스에서 활성화
- `BaseEntity`: 생성일/수정일 자동 관리
- `@CreatedDate`, `@LastModifiedDate`: 자동 날짜 설정

### 페이징 처리
- Spring Data JPA의 `Pageable` 인터페이스 활용
- `PageRequest.of()`: 페이지 번호, 크기, 정렬 조건 설정
- Thymeleaf에서 페이지네이션 UI 구현

### 권한 검증
- 게시글/댓글 수정/삭제 시 작성자 권한 확인
- `UnauthorizedAccessException`: 권한 없음 예외 처리
- Spring Security의 인증 정보 활용

### 개인정보 보호
- `MaskingUtil`: 사용자 정보 마스킹 처리
- 이메일, 전화번호 등 민감 정보 부분 숨김

## 데이터베이스 설정

### MySQL 설정
1. MySQL 서버 설치 및 실행
2. 데이터베이스 생성:
   ```sql
   CREATE DATABASE demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. `application.yml` 파일에서 데이터베이스 연결 정보 수정:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/demo_db?serverTimezone=Asia/Seoul
       username: your_username
       password: your_password
       driver-class-name: com.mysql.cj.jdbc.Driver
   ```

## 실행 방법

### 1. 사전 요구사항
- Java 17 이상
- Maven 3.6 이상
- MySQL 8.0 이상

### 2. 프로젝트 클론 및 실행
```bash
# 프로젝트 클론
git clone <repository-url>
cd board-project-demo

# 데이터베이스 연결 정보 수정
# src/main/resources/application.yml 파일 편집

# Maven을 통한 의존성 설치 및 실행
./mvnw clean compile
./mvnw spring-boot:run

# 또는 JAR 파일 생성 후 실행
./mvnw clean package
java -jar target/board-project-demo-0.0.1-SNAPSHOT.jar
```

### 3. 애플리케이션 접속
- URL: http://localhost:8080
- 회원가입 후 로그인하여 게시판 기능 이용

## 개발 환경 설정

### IDE 설정
- **Eclipse**: Spring Tools 4 플러그인 설치 권장
- **IntelliJ IDEA**: Spring Boot 플러그인 활성화
- **Lombok**: IDE에 Lombok 플러그인 설치 필수

### 개발 도구
- **Spring Boot DevTools**: 자동 재시작 및 LiveReload
- **JPA DDL**: `hibernate.ddl-auto=update` (개발 환경)
- **SQL 로깅**: `show-sql=true`, `format_sql=true`

## 보안 기능

### Spring Security 설정
- **인증**: 폼 기반 로그인 (`/user/login`)
- **인가**: URL 패턴별 접근 권한 설정
- **로그아웃**: `/user/logout` 경로
- **CSRF**: 기본 활성화 (POST 요청 보호)

### 비밀번호 보안
- **BCryptPasswordEncoder**: 단방향 암호화
- **Salt**: 자동 생성으로 레인보우 테이블 공격 방지

## API 엔드포인트

### 사용자 관리
- `GET /user/signup`: 회원가입 폼
- `POST /user/signup`: 회원가입 처리
- `GET /user/login`: 로그인 폼
- `POST /user/login`: 로그인 처리

### 게시판 관리
- `GET /board/list`: 게시글 목록 (페이징)
- `GET /board/detail/{id}`: 게시글 상세
- `GET /board/create`: 게시글 작성 폼
- `POST /board/create`: 게시글 작성 처리
- `GET /board/modify/{id}`: 게시글 수정 폼
- `POST /board/modify/{id}`: 게시글 수정 처리
- `POST /board/delete/{id}`: 게시글 삭제

### 댓글 관리
- `POST /comment/create`: 댓글 작성
- `POST /comment/delete/{id}`: 댓글 삭제

## 향후 개선 사항

### 기능 확장
- 파일 업로드 기능 (이미지, 첨부파일)
- 게시글 검색 기능 (제목, 내용, 작성자)
- 댓글 수정 기능
- 게시글 카테고리 분류
- 좋아요/싫어요 기능

### 관리 기능
- 관리자 페이지 (사용자/게시글 관리)
- 통계 대시보드
- 로그 관리 시스템

### API 확장
- REST API 지원 (JSON 응답)
- API 문서화 (Swagger/OpenAPI)
- 모바일 앱 연동

### 성능 최적화
- Redis 캐싱 적용
- 데이터베이스 인덱스 최적화
- 이미지 CDN 연동
- 페이지 로딩 속도 개선

### 보안 강화
- JWT 토큰 기반 인증
- OAuth2 소셜 로그인
- API Rate Limiting
- XSS 방지 강화
