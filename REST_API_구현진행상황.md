# REST API + Swagger 구현 진행 상황

**프로젝트:** board-project-demo  
**작업 시작일:** 2025-12-15  
**작업 내용:** 기존 MVC 프로젝트에 REST API + JWT 인증 + Swagger 문서화 추가

---

## 📋 전체 진행 상황

| Phase | 작업 내용 | 상태 | 완료일 |
|-------|----------|------|--------|
| Phase 1 | 기본 설정 및 인프라 | ✅ 완료 | 2025-12-15 |
| Phase 2 | 인증 API 구현 | ✅ 완료 | 2025-12-16 |
| Phase 3 | 사용자 관리 API | ✅ 완료 | 2025-12-16 |
| Phase 4 | 게시판 API | ✅ 완료 | 2025-12-17 |
| Phase 5 | 댓글 API | ⏳ 대기 | - |
| Phase 6 | 문서화 및 테스트 | ⏳ 대기 | - |

---

## ✅ Phase 1: 기본 설정 및 인프라 (완료)

### 1.1 의존성 추가 ✅

**파일:** `/pom.xml`

**추가된 의존성:**
```xml
<!-- Swagger/OpenAPI 3.0: 자동 API 문서 생성 -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.14</version>
</dependency>

<!-- JWT 토큰 인증 라이브러리 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

---

### 1.2 SwaggerConfig.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/config/SwaggerConfig.java`

**주요 기능:**
- API 문서 자동 생성 설정
- JWT Bearer 토큰 인증 스키마 정의
- API 기본 정보 설정 (제목, 설명, 버전)

**접속 정보:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

### 1.3 JwtConfig.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/config/JwtConfig.java`

**주요 기능:**
- JWT 토큰 설정값 중앙 관리
- `@ConfigurationProperties`로 application.yml과 연동
- 토큰 만료시간, 비밀키 등 설정

**설정값:**
- Access Token 만료: 1시간 (3600000ms)
- Refresh Token 만료: 7일 (604800000ms)
- 비밀키: 32자 이상의 보안 문자열

---

### 1.4 application.yml 설정 추가 ✅

**파일:** `/src/main/resources/application.yml`

**추가된 설정:**
```yaml
# JWT 토큰 설정
jwt:
  secret: myBoardProjectSecretKey123456789012345678901234567890
  access-token-expiration: 3600000
  refresh-token-expiration: 604800000
```

---

### 1.5 JwtTokenProvider.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/jwt/JwtTokenProvider.java`

**주요 기능:**
- JWT Access Token 생성 (1시간 만료)
- JWT Refresh Token 생성 (7일 만료)
- 토큰에서 사용자 ID/권한 추출
- 토큰 유효성 검증 (만료, 서명, 형식 체크)

**핵심 메서드:**
- `createAccessToken()` - 로그인 성공 시 토큰 발급
- `validateToken()` - API 요청 시 토큰 검증
- `getUserIdFromToken()` - 토큰에서 사용자 정보 추출

---

### 1.6 JwtAuthenticationFilter.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/jwt/JwtAuthenticationFilter.java`

**주요 기능:**
- HTTP 요청 헤더에서 JWT 토큰 추출
- 토큰 유효성 검증 후 Spring Security Context 설정
- `OncePerRequestFilter` 상속으로 요청당 한 번만 실행

**처리 플로우:**
1. Authorization 헤더에서 "Bearer " 토큰 추출
2. 토큰 유효성 검증
3. 토큰에서 사용자 정보/권한 추출
4. Spring Security 인증 객체 생성 및 설정

---

### 1.7 ApiResponseDto.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/dto/ApiResponseDto.java`

**주요 기능:**
- REST API 표준 응답 형식 정의
- 성공/실패 응답 통일화
- 제네릭 타입으로 다양한 데이터 타입 지원

**응답 구조:**
```json
{
  "success": true,
  "data": { /* 실제 데이터 */ },
  "message": "성공 메시지",
  "timestamp": "2025-12-15T17:20:00"
}
```

---

## ✅ Phase 2: 인증 API 구현 (완료)

### 2.1 LoginRequestDto.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/dto/LoginRequestDto.java`

**주요 기능:**
- REST API 로그인 요청 데이터 수신
- JSON 형태의 로그인 정보를 Java 객체로 변환
- `@NotBlank` 검증으로 필수 입력값 체크

**요청 형식:**
```json
{
  "userId": "admin01",
  "password": "password123"
}
```

---

### 2.2 LoginResponseDto.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/dto/LoginResponseDto.java`

**주요 기능:**
- 로그인 성공 시 JWT 토큰과 사용자 정보 반환
- Access Token, Refresh Token 포함
- 클라이언트 UI용 사용자 기본 정보 제공

**응답 형식:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "userNo": 1,
    "userId": "admin01",
    "name": "관리자",
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}
```

---

### 2.3 SignupRequestDto.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/dto/SignupRequestDto.java`

**주요 기능:**
- REST API 회원가입 요청 데이터 수신
- 기존 UserSignupDto와 분리된 API 전용 DTO
- 이메일 형식 검증 및 필수 입력값 체크

**요청 형식:**
```json
{
  "userId": "newuser01",
  "password": "password123",
  "passwordConfirm": "password123",
  "name": "홍길동",
  "email": "user@example.com"
}
```

---

### 2.4 UserService.java 확장 ✅

**추가된 메서드:**
```java
// REST API용 사용자 조회
@Transactional(readOnly = true)
public User getUserByUserId(String userId)

// REST API용 회원가입 (오버로딩)
@Transactional
public User create(SignupRequestDto signupRequest)
```

**목적:** REST API와 기존 MVC의 독립적 운영

---

### 2.5 AuthRestController.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/controller/AuthRestController.java`

**주요 API:**
- `POST /api/v1/auth/login` - JWT 토큰 기반 로그인
- `POST /api/v1/auth/signup` - REST API 회원가입

**핵심 기능:**
- Spring Security AuthenticationManager를 통한 인증
- JWT 토큰 생성 및 발급
- 표준화된 JSON 응답 형식
- 입력값 검증 및 오류 처리

**인증 플로우:**
1. 클라이언트 → 로그인 요청 (JSON)
2. Spring Security → 사용자 인증
3. JWT 토큰 생성 및 발급
4. 토큰 + 사용자 정보 응답

---

### 2.6 RestSecurityConfig.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/config/RestSecurityConfig.java`

**주요 설정:**
- REST API 전용 보안 설정 (`/api/**` 경로만 적용)
- JWT 토큰 기반 Stateless 인증
- 기존 MVC 보안 설정과 독립적 운영

**보안 정책:**
```
/api/v1/auth/**     → 모든 사용자 (로그인/회원가입)
/api/v1/admin/**    → ADMIN 권한만
/api/v1/users/**    → ADMIN 권한만
/api/v1/boards      → 인증된 사용자 (작성/수정/삭제)
/api/v1/boards      → 모든 사용자 (조회)
```

**핵심 특징:**
- `@Order(1)`: 기존 SecurityConfig보다 우선 적용
- `SessionCreationPolicy.STATELESS`: 세션 사용 안 함
- JWT 필터 체인 적용

---

### 2.7 RestExceptionHandler.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/exception/RestExceptionHandler.java`

**주요 기능:**
- REST API 전역 예외 처리
- 일관된 오류 응답 형식 제공
- HTTP 상태코드별 적절한 오류 메시지

**처리하는 예외들:**
- `MethodArgumentNotValidException` → 400 (입력값 검증 오류)
- `AuthenticationException` → 401 (인증 실패)
- `AccessDeniedException` → 403 (권한 부족)
- `IllegalStateException` → 400 (비즈니스 로직 오류)
- `Exception` → 500 (서버 내부 오류)

---

## ✅ Phase 3: 사용자 관리 API (완료)

### 3.1 UserListResponseDto.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/dto/UserListResponseDto.java`

**주요 기능:**
- 회원 목록 조회 시 JSON 응답용 DTO
- 민감한 정보(비밀번호) 제외하고 필요한 정보만 포함
- User 엔티티를 안전하게 API 응답으로 변환

**응답 필드:**
- `userNo`, `userId`, `name`, `email`, `role`, `createDt`, `modifyDt`

---

### 3.2 UserRoleUpdateRequestDto.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/dto/UserRoleUpdateRequestDto.java`

**주요 기능:**
- 사용자 권한 변경 요청 데이터 수신
- Role enum 타입으로 타입 안정성 보장
- `@NotNull` 검증으로 필수값 체크

**요청 형식:**
```json
{
  "role": "ADMIN"
}
```

---

### 3.3 UserService.java 확장 ✅

**추가된 메서드:**
```java
// REST API용 모든 사용자 목록 조회
@Transactional(readOnly = true)
public List<User> getAllUsersForApi()

// 사용자 번호로 User 엔티티 조회
@Transactional(readOnly = true)
public User getUserByUserNo(Long userNo)
```

**목적:** REST API 전용 데이터 조회 메서드 제공

---

### 3.4 UserRestController.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/controller/UserRestController.java`

**구현된 API:**
- `GET /api/v1/users` - 회원 목록 조회 (ADMIN만)
- `PUT /api/v1/users/{userNo}/role` - 권한 변경 (ADMIN만)
- `GET /api/v1/users/me` - 내 정보 조회 (로그인 사용자)

**핵심 기능:**
- `@PreAuthorize`를 통한 메서드 레벨 권한 검증
- 현재 로그인 사용자를 목록 맨 위로 정렬
- 자기 자신의 권한 변경 방지
- 표준화된 JSON 응답 형식

---

## ✅ Phase 4: 게시판 API (완료)

### 4.1 BoardCreateRequestDto.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/dto/BoardCreateRequestDto.java`

**주요 기능:**
- REST API 게시글 작성 요청 데이터 수신
- JSON 형태의 게시글 정보를 Java 객체로 변환
- `@NotBlank`, `@Size` 검증으로 입력값 체크

**요청 형식:**
```json
{
  "title": "게시글 제목",
  "content": "게시글 내용 (최소 10자 이상)"
}
```

---

### 4.2 BoardUpdateRequestDto.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/dto/BoardUpdateRequestDto.java`

**주요 기능:**
- REST API 게시글 수정 요청 데이터 수신
- 작성 DTO와 동일한 검증 규칙 적용
- 제목 최대 300자, 내용 최소 10자 제한

**요청 형식:**
```json
{
  "title": "수정된 게시글 제목",
  "content": "수정된 게시글 내용"
}
```

---

### 4.3 BoardService.java 확장 ✅

**추가된 메서드들:**
- REST API용 게시글 CRUD 메서드들 확장 완료
- 페이징 처리 및 권한 검증 로직 포함
- 기존 MVC와 독립적인 API 전용 메서드 제공

---

### 4.4 BoardRestController.java 생성 ✅

**파일:** `/src/main/java/com/example/demo/controller/BoardRestController.java`

**구현된 API:**
- `GET /api/v1/boards` - 게시글 목록 (페이징) ✅
- `POST /api/v1/boards` - 게시글 작성 (인증 필요) ✅
- `GET /api/v1/boards/{boardNo}` - 게시글 상세 조회 ✅
- `PUT /api/v1/boards/{boardNo}` - 게시글 수정 (작성자만) ✅
- `DELETE /api/v1/boards/{boardNo}` - 게시글 삭제 (작성자/ADMIN) ✅

**핵심 권한 정책:**
- **게시글 조회**: 모든 사용자 가능 (인증 불필요)
- **게시글 작성**: GUEST/ADMIN 권한 필요
- **게시글 수정**: 본인만 가능 (ADMIN도 불가)
- **게시글 삭제**: 본인 또는 ADMIN 가능

---

### 4.5 RestSecurityConfig.java 권한 설정 수정 ✅

**수정 내용:**
- HTTP 메서드별 권한 구분 설정
- GET 요청은 인증 불필요 (`permitAll()`)
- POST/PUT/DELETE 요청은 인증 필요 (`hasAnyRole()`)

**추가된 import:**
```java
import org.springframework.http.HttpMethod;
```

**수정된 권한 설정:**
```java
// 게시글 조회는 모든 사용자 허용
.requestMatchers(HttpMethod.GET, "/api/v1/boards").permitAll()
.requestMatchers(HttpMethod.GET, "/api/v1/boards/*").permitAll()

// 게시글 작성/수정/삭제는 인증된 사용자만
.requestMatchers(HttpMethod.POST, "/api/v1/boards").hasAnyRole("GUEST", "ADMIN")
.requestMatchers(HttpMethod.PUT, "/api/v1/boards/*").hasAnyRole("GUEST", "ADMIN")
.requestMatchers(HttpMethod.DELETE, "/api/v1/boards/*").hasAnyRole("GUEST", "ADMIN")
```

---

### 4.6 API 테스트 완료 ✅

**테스트 시나리오:**
1. ✅ 회원가입 (`POST /api/v1/auth/signup`)
2. ✅ 로그인 및 JWT 토큰 발급 (`POST /api/v1/auth/login`)
3. ✅ JWT 토큰 인증 설정 (Swagger UI)
4. ✅ 게시글 작성 (`POST /api/v1/boards`)
5. ✅ 게시글 목록 조회 (`GET /api/v1/boards`)
6. ✅ 게시글 상세 조회 (`GET /api/v1/boards/{boardNo}`)
7. ✅ 로그아웃 후 조회 권한 확인

**해결된 이슈:**
- JWT 토큰 공백 문제 해결
- SpringDoc OpenAPI 버전 호환성 문제 해결 (2.2.0 → 2.8.14)
- 권한 설정 오류 수정 (USER → GUEST)
- HTTP 메서드별 권한 구분 설정

---

**마지막 업데이트:** 2025-12-17 15:23 (Phase 4 완료)

---

## 🔄 다음 작업: Phase 5 (댓글 API)

**구현 예정:**
- GET `/api/v1/boards/{boardNo}/comments` - 댓글 목록 조회
- POST `/api/v1/boards/{boardNo}/comments` - 댓글 작성 (인증 필요)
- DELETE `/api/v1/boards/{boardNo}/comments/{commentNo}` - 댓글 삭제 (작성자/ADMIN)

**필요한 작업:**
1. **CommentCreateRequestDto.java** 생성 - 댓글 작성 요청 DTO
2. **CommentRestController.java** 생성 - 댓글 REST API 컨트롤러
3. **CommentService.java** 확장 - REST API용 메서드 추가
4. **권한 검증 로직** - 댓글 삭제 시 작성자/ADMIN 권한 체크

**예상 소요 시간:** 1-2시간


