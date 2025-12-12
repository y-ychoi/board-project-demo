# Board Project Demo

## 1. 프로젝트 소개

Spring Boot와 Spring Security를 활용한 게시판 웹 애플리케이션입니다. 사용자 인증, 게시글 CRUD, 댓글 시스템을 포함한 완전한 게시판 기능을 제공합니다.

## 2. 주요 기능

### 사용자 관리
- 회원가입 및 로그인 (BCrypt 암호화)
- Spring Security 기반 인증/인가
- 개인정보 마스킹 처리

### 게시판 기능
- 게시글 CRUD (작성자 권한 검증)
- 페이징 처리된 목록 조회
- 조회수 증가 기능

### 댓글 시스템
- 댓글 작성 및 삭제
- 작성자 권한 검증

## 3. 기술 스택

- **Backend**: Spring Boot 3.4.11, Java 17
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA, Hibernate
- **Security**: Spring Security 6
- **Template Engine**: Thymeleaf
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
- URL: http://localhost:8080
- 회원가입 후 로그인하여 게시판 기능 이용

## 5. 프로젝트 구조

```
board-project-demo/
├── src/main/java/com/example/demo/
│   ├── BoardProjectDemoApplication.java    # 메인 애플리케이션 클래스
│   ├── config/
│   │   └── SecurityConfig.java             # Spring Security 설정
│   ├── controller/                         # 웹 요청 처리
│   │   ├── BoardController.java
│   │   ├── CommentController.java
│   │   └── UserController.java
│   ├── dto/                               # 데이터 전송 객체
│   │   ├── BoardDetailResponseDto.java
│   │   ├── BoardListResponseDto.java
│   │   └── CommentResponseDto.java
│   ├── entity/                            # JPA 엔티티
│   │   ├── BaseEntity.java
│   │   ├── Board.java
│   │   ├── Comment.java
│   │   └── User.java
│   ├── repository/                        # 데이터 접근 계층
│   │   ├── BoardRepository.java
│   │   ├── CommentRepository.java
│   │   └── UserRepository.java
│   ├── service/                           # 비즈니스 로직
│   │   ├── BoardService.java
│   │   ├── CommentService.java
│   │   └── UserService.java
│   ├── security/                          # 보안 관련
│   │   └── UserSecurityService.java
│   ├── exception/                         # 예외 처리
│   │   └── UnauthorizedAccessException.java
│   └── util/                             # 유틸리티
│       └── MaskingUtil.java
├── src/main/resources/
│   ├── application.yml                    # 애플리케이션 설정
│   ├── static/                           # 정적 리소스
│   └── templates/                        # Thymeleaf 템플릿
│       ├── index.html
│       ├── login_form.html
│       ├── signup_form.html
│       └── board/
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

- **인증 필터**: 사용자 자격 증명 검증
- **인가 필터**: URL 패턴별 접근 권한 확인
- **CSRF 필터**: Cross-Site Request Forgery 공격 방지
- **세션 관리**: 세션 기반 상태 유지

#### 보안 구성요소
- **PasswordEncoder**: BCrypt 알고리즘을 통한 단방향 암호화
- **UserDetailsService**: 사용자 정보 로딩 및 권한 부여
- **SecurityContext**: 인증된 사용자 정보 저장 및 관리

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

### 사용자 관리
| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| GET | `/user/signup` | 회원가입 폼 | X |
| POST | `/user/signup` | 회원가입 처리 | X |
| GET | `/user/login` | 로그인 폼 | X |
| POST | `/user/login` | 로그인 처리 | X |
| POST | `/user/logout` | 로그아웃 | O |

### 게시판 관리
| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| GET | `/board/list` | 게시글 목록 (페이징) | X |
| GET | `/board/detail/{id}` | 게시글 상세 조회 | X |
| GET | `/board/create` | 게시글 작성 폼 | O |
| POST | `/board/create` | 게시글 작성 처리 | O |
| GET | `/board/modify/{id}` | 게시글 수정 폼 | O |
| POST | `/board/modify/{id}` | 게시글 수정 처리 | O |
| POST | `/board/delete/{id}` | 게시글 삭제 | O |

### 댓글 관리
| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| POST | `/comment/create` | 댓글 작성 | O |
| POST | `/comment/delete/{id}` | 댓글 삭제 | O |

### 요청/응답 예시

#### 게시글 목록 조회
```
GET /board/list?page=0&size=10

Response: HTML (Thymeleaf 렌더링)
- 페이징된 게시글 목록
- 페이지네이션 UI
- 검색 기능 (향후 구현 예정)
```

#### 게시글 작성
```
POST /board/create
Content-Type: application/x-www-form-urlencoded

title=게시글 제목&content=게시글 내용

Response: Redirect to /board/list
```

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
```

#### 연관관계
- User : Board = 1 : N (한 사용자는 여러 게시글 작성 가능)
- User : Comment = 1 : N (한 사용자는 여러 댓글 작성 가능)
- Board : Comment = 1 : N (한 게시글에 여러 댓글 작성 가능)

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
- 댓글 수정 기능
- 게시글 카테고리 분류
- 좋아요/싫어요 기능

#### 성능 최적화
- Redis 캐싱 적용
- 데이터베이스 인덱스 최적화
- 이미지 CDN 연동
- 페이지 로딩 속도 개선

#### 보안 강화
- JWT 토큰 기반 인증
- OAuth2 소셜 로그인
- API Rate Limiting
- XSS 방지 강화
