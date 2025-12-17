# REST API + Swagger 개발 계획서

## 1. 개요

### 목적
기존 MVC 패턴의 게시판 프로젝트를 REST API로 확장하여 모바일 앱, SPA 등 다양한 클라이언트에서 사용할 수 있도록 구현

### 주요 기능
- JWT 토큰 기반 인증/인가 시스템
- RESTful API 설계 원칙 준수
- Swagger를 통한 자동 API 문서화
- 기존 MVC와 병행 운영 (하이브리드 구조)

---

## 2. 기술 스택

### 추가될 기술
- **API 문서화**: SpringDoc OpenAPI 3.0 (Swagger)
- **인증**: JWT (JSON Web Token)
- **보안**: Spring Security + JWT Filter
- **응답 형식**: JSON (RESTful)

### 기존 기술 유지
- **Backend**: Spring Boot 3.4.11, Java 17
- **Database**: MySQL 8.0 + Spring Data JPA
- **Build**: Maven

---

## 3. API 설계

### 3.1 Base URL
```
http://localhost:8080/api/v1
```

### 3.2 인증 API (/auth)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| POST | `/auth/signup` | 회원가입 | X |
| POST | `/auth/login` | 로그인 (JWT 토큰 발급) | X |
| POST | `/auth/logout` | 로그아웃 (토큰 무효화) | O |
| POST | `/auth/refresh` | 토큰 갱신 | O |

#### 로그인 요청/응답 예시
```json
// POST /api/v1/auth/login
{
  "userId": "admin01",
  "password": "password123"
}

// Response
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

### 3.3 사용자 관리 API (/users)

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | `/users` | 회원 목록 조회 | ADMIN |
| GET | `/users/me` | 내 정보 조회 | USER |
| PUT | `/users/{userNo}/role` | 권한 변경 | ADMIN |
| GET | `/users/{userNo}` | 특정 회원 조회 | ADMIN |

#### 회원 목록 응답 예시
```json
// GET /api/v1/users
{
  "success": true,
  "data": [
    {
      "userNo": 1,
      "userId": "admin01",
      "name": "관리자",
      "email": "admin@example.com",
      "role": "ADMIN",
      "createDt": "2025-12-15T10:30:00"
    }
  ],
  "message": "회원 목록 조회 성공"
}
```

### 3.4 게시판 API (/boards)

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | `/boards` | 게시글 목록 (페이징) | ALL |
| GET | `/boards/{boardNo}` | 게시글 상세 조회 | ALL |
| POST | `/boards` | 게시글 작성 | USER |
| PUT | `/boards/{boardNo}` | 게시글 수정 | OWNER만 |
| DELETE | `/boards/{boardNo}` | 게시글 삭제 | OWNER/ADMIN |

**⚠️ 중요**: ADMIN은 게시글 수정 불가 (403 Forbidden), 삭제만 가능

#### 게시글 목록 응답 예시
```json
// GET /api/v1/boards?page=0&size=10
{
  "success": true,
  "data": {
    "content": [
      {
        "boardNo": 1,
        "title": "게시글 제목",
        "content": "게시글 내용",
        "viewCnt": 10,
        "authorName": "작성자",
        "createDt": "2025-12-15T10:30:00"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 10,
      "totalElements": 50,
      "totalPages": 5
    }
  }
}
```

### 3.5 댓글 API (/comments)

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | `/boards/{boardNo}/comments` | 댓글 목록 | ALL |
| POST | `/boards/{boardNo}/comments` | 댓글 작성 | USER |
| DELETE | `/boards/{boardNo}/comments/{commentNo}` | 댓글 삭제 | OWNER/ADMIN |

---

## 4. 보안 설계

### 4.1 JWT 토큰 구조
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "admin01",
    "userNo": 1,
    "role": "ADMIN",
    "iat": 1702641600,
    "exp": 1702645200
  }
}
```

### 4.2 인증 플로우
```
1. 클라이언트 → POST /auth/login (아이디/비밀번호)
2. 서버 → JWT 토큰 발급 및 응답
3. 클라이언트 → API 요청 시 Header에 토큰 포함
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
4. 서버 → 토큰 검증 후 API 처리
```

### 4.3 권한 체계
- **ALL**: 모든 사용자 (비로그인 포함)
- **USER**: 로그인한 사용자 (GUEST + ADMIN)
- **ADMIN**: 관리자 권한
  - 모든 게시글/댓글 **삭제** 가능
  - 게시글/댓글 **수정은 불가** (작성자만 가능)
- **OWNER**: 본인이 작성한 게시글/댓글 (수정/삭제 모두 가능)

---

## 5. 개발 범위

### 5.1 새로 생성할 파일들

#### Configuration
- `SwaggerConfig.java` - Swagger 설정
- `JwtConfig.java` - JWT 설정
- `RestSecurityConfig.java` - REST API 보안 설정

#### JWT 관련
- `JwtTokenProvider.java` - JWT 토큰 생성/검증
- `JwtAuthenticationFilter.java` - JWT 인증 필터
- `JwtAuthenticationEntryPoint.java` - 인증 실패 처리

#### REST Controllers
- `AuthRestController.java` - 인증 API
- `UserRestController.java` - 사용자 관리 API
- `BoardRestController.java` - 게시판 API
- `CommentRestController.java` - 댓글 API

#### DTOs (Request/Response)
- `LoginRequestDto.java` - 로그인 요청
- `LoginResponseDto.java` - 로그인 응답
- `ApiResponseDto.java` - 공통 응답 형식
- `BoardCreateRequestDto.java` - 게시글 작성 요청
- `BoardResponseDto.java` - 게시글 응답

#### Exception Handling
- `RestExceptionHandler.java` - REST API 예외 처리
- `ApiException.java` - API 전용 예외 클래스

### 5.2 수정할 파일들
- `pom.xml` - 의존성 추가
- `application.yml` - JWT 설정 추가
- `SecurityConfig.java` - REST API 경로 추가

---

## 6. 개발 순서

### Phase 1: 기본 설정 및 인프라 (1일)
1. **의존성 추가**
   - SpringDoc OpenAPI
   - JWT 라이브러리
   - Maven 업데이트

2. **기본 설정 클래스 생성**
   - SwaggerConfig.java
   - JwtConfig.java
   - ApiResponseDto.java (공통 응답 형식)

3. **JWT 인프라 구축**
   - JwtTokenProvider.java
   - JwtAuthenticationFilter.java
   - JwtAuthenticationEntryPoint.java

### Phase 2: 인증 API 구현 (1일)
4. **AuthRestController 구현**
   - POST /auth/signup
   - POST /auth/login
   - POST /auth/logout

5. **보안 설정 통합**
   - RestSecurityConfig.java
   - JWT 필터 체인 적용

6. **예외 처리**
   - RestExceptionHandler.java
   - 인증 관련 예외 처리

### Phase 3: 사용자 관리 API (0.5일)
7. **UserRestController 구현**
   - GET /users (관리자 전용)
   - PUT /users/{userNo}/role
   - GET /users/me

8. **권한 검증 로직**
   - @PreAuthorize 적용
   - 관리자 권한 체크

### Phase 4: 게시판 API (1일)
9. **BoardRestController 구현**
   - GET /boards (페이징)
   - POST /boards
   - GET /boards/{boardNo}
   - PUT /boards/{boardNo}
   - DELETE /boards/{boardNo}

10. **작성자 권한 검증**
    - 게시글 **수정**: 본인만 가능 (ADMIN도 불가)
    - 게시글 **삭제**: 본인 또는 ADMIN 가능
    - 관리자는 모든 게시글 **삭제만** 가능

### Phase 5: 댓글 API (0.5일)
11. **CommentRestController 구현**
    - GET /boards/{boardNo}/comments
    - POST /boards/{boardNo}/comments
    - DELETE /comments/{commentNo}

### Phase 6: 문서화 및 테스트 (1일)
12. **Swagger 어노테이션 추가**
    - @Operation, @ApiResponse 등
    - API 문서 상세화

13. **통합 테스트**
    - Swagger UI에서 전체 API 테스트
    - 권한별 접근 제어 확인

---

## 7. API 응답 형식 표준화

### 7.1 성공 응답
```json
{
  "success": true,
  "data": { /* 실제 데이터 */ },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-12-15T16:00:00"
}
```

### 7.2 오류 응답
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "아이디 또는 비밀번호가 올바르지 않습니다.",
    "details": "로그인 정보를 다시 확인해주세요."
  },
  "timestamp": "2025-12-15T16:00:00"
}
```

### 7.3 페이징 응답
```json
{
  "success": true,
  "data": {
    "content": [ /* 데이터 배열 */ ],
    "pageable": {
      "page": 0,
      "size": 10,
      "totalElements": 100,
      "totalPages": 10,
      "first": true,
      "last": false
    }
  }
}
```

---

## 8. Swagger 문서 구성

### 8.1 API 그룹화
- **🔐 Authentication**: 인증 관련 API
- **👥 User Management**: 사용자 관리 API  
- **📝 Board**: 게시판 API
- **💬 Comment**: 댓글 API

### 8.2 보안 스키마
```yaml
securitySchemes:
  bearerAuth:
    type: http
    scheme: bearer
    bearerFormat: JWT
```

### 8.3 접속 정보
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 9. 테스트 시나리오

### 9.1 인증 테스트
1. 회원가입 → 기본 GUEST 권한 확인
2. 로그인 → JWT 토큰 발급 확인
3. 토큰으로 인증 필요 API 호출
4. 만료된 토큰으로 API 호출 → 401 오류
5. 잘못된 토큰으로 API 호출 → 401 오류

### 9.2 권한 테스트
1. GUEST로 관리자 API 호출 → 403 오류
2. ADMIN으로 모든 API 호출 → 성공
3. ADMIN이 타인의 게시글 **수정** 시도 → 403 오류 (접근 차단)
4. ADMIN이 타인의 게시글 **삭제** 시도 → 성공
5. 본인 게시글 수정/삭제 → 성공

### 9.3 CRUD 테스트
1. 게시글 목록 조회 (페이징)
2. 게시글 작성 → 로그인 필요
3. 게시글 상세 조회 → 조회수 증가
4. 게시글 수정/삭제 → 작성자 권한 확인

---

## 10. 예상 소요 시간

| Phase | 작업 내용 | 예상 시간 |
|-------|----------|----------|
| Phase 1 | 기본 설정 및 JWT 인프라 | 8시간 |
| Phase 2 | 인증 API 구현 | 6시간 |
| Phase 3 | 사용자 관리 API | 3시간 |
| Phase 4 | 게시판 API | 6시간 |
| Phase 5 | 댓글 API | 3시간 |
| Phase 6 | 문서화 및 테스트 | 4시간 |
| **총 예상 시간** | **30시간 (약 4일)** |

---

## 11. 체크리스트

### Phase 1: 기본 설정
- [ ] pom.xml 의존성 추가
- [ ] SwaggerConfig.java 생성
- [ ] JwtConfig.java 생성
- [ ] JwtTokenProvider.java 생성
- [ ] JwtAuthenticationFilter.java 생성
- [ ] ApiResponseDto.java 생성

### Phase 2: 인증 API
- [ ] AuthRestController.java 생성
- [ ] LoginRequestDto/ResponseDto 생성
- [ ] RestSecurityConfig.java 생성
- [ ] RestExceptionHandler.java 생성
- [ ] 로그인/회원가입 API 테스트

### Phase 3: 사용자 관리 API
- [ ] UserRestController.java 생성
- [ ] 회원 목록 조회 API
- [ ] 권한 변경 API
- [ ] 관리자 권한 검증

### Phase 4: 게시판 API
- [ ] BoardRestController.java 생성
- [ ] 게시글 CRUD API 구현
- [ ] 페이징 처리
- [ ] 작성자 권한 검증

### Phase 5: 댓글 API
- [ ] CommentRestController.java 생성
- [ ] 댓글 CRUD API 구현
- [ ] 게시글별 댓글 조회

### Phase 6: 문서화 및 테스트
- [ ] Swagger 어노테이션 추가
- [ ] API 문서 완성
- [ ] 전체 기능 테스트
- [ ] 권한별 접근 제어 확인

---

## 12. 참고 사항

### 기존 MVC와의 관계
- **기존 MVC 컨트롤러**: 웹 페이지용으로 유지
- **새로운 REST 컨트롤러**: API용으로 추가
- **공통 Service/Repository**: 재사용

### URL 구분
```
웹 페이지: /user/login, /board/list
REST API: /api/v1/auth/login, /api/v1/boards
```

### 향후 확장 가능성
- 모바일 앱 개발
- React/Vue.js SPA 개발
- 외부 시스템 연동
- 마이크로서비스 아키텍처 전환

---

**작성일**: 2025-12-15  
**작성자**: 개발팀  
**버전**: 1.0
