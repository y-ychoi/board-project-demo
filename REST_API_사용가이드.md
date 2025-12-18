# REST API 사용 가이드

**프로젝트**: board-project-demo  
**버전**: 1.0  
**작성일**: 2025-12-18

---

## 🚀 Quick Start

### 1. 서버 실행
```bash
cd board-project-demo
./mvnw spring-boot:run
```

### 2. API 문서 확인
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

---

## 🔐 인증 방법

### JWT 토큰 기반 인증

#### 1단계: 회원가입
```bash
POST /api/v1/auth/signup
Content-Type: application/json

{
  "userId": "testuser01",
  "password": "password123",
  "passwordConfirm": "password123",
  "name": "테스트사용자",
  "email": "test@example.com"
}
```

#### 2단계: 로그인 및 토큰 발급
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "userId": "testuser01",
  "password": "password123"
}
```

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userNo": 1,
      "userId": "testuser01",
      "name": "테스트사용자",
      "email": "test@example.com",
      "role": "GUEST"
    }
  }
}
```

#### 3단계: API 호출 시 토큰 사용
```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📝 주요 API 사용 예제

### 게시글 관리

#### 게시글 목록 조회 (인증 불필요)
```bash
GET /api/v1/boards?page=0&size=10
```

#### 게시글 작성 (인증 필요)
```bash
POST /api/v1/boards
Authorization: Bearer {토큰}
Content-Type: application/json

{
  "title": "게시글 제목",
  "content": "게시글 내용입니다. 최소 10자 이상 작성해주세요."
}
```

#### 게시글 상세 조회 (인증 불필요)
```bash
GET /api/v1/boards/1
```

#### 게시글 수정 (작성자만)
```bash
PUT /api/v1/boards/1
Authorization: Bearer {토큰}
Content-Type: application/json

{
  "title": "수정된 제목",
  "content": "수정된 내용입니다."
}
```

#### 게시글 삭제 (작성자 또는 ADMIN)
```bash
DELETE /api/v1/boards/1
Authorization: Bearer {토큰}
```

### 댓글 관리

#### 댓글 목록 조회 (인증 불필요)
```bash
GET /api/v1/boards/1/comments
```

#### 댓글 작성 (인증 필요)
```bash
POST /api/v1/boards/1/comments
Authorization: Bearer {토큰}
Content-Type: application/json

{
  "content": "댓글 내용입니다!"
}
```

#### 댓글 삭제 (작성자 또는 ADMIN)
```bash
DELETE /api/v1/boards/1/comments/1
Authorization: Bearer {토큰}
```

### 사용자 관리 (ADMIN 전용)

#### 회원 목록 조회
```bash
GET /api/v1/users
Authorization: Bearer {ADMIN_토큰}
```

#### 사용자 권한 변경
```bash
PUT /api/v1/users/1/role
Authorization: Bearer {ADMIN_토큰}
Content-Type: application/json

{
  "role": "ADMIN"
}
```

---

## 🔒 권한 체계

### 권한 레벨
- **ALL**: 모든 사용자 (비로그인 포함)
- **GUEST**: 일반 사용자
- **ADMIN**: 관리자

### API별 권한 요구사항

| API | 권한 | 설명 |
|-----|------|------|
| 게시글 조회 | ALL | 인증 불필요 |
| 게시글 작성 | GUEST/ADMIN | 로그인 필요 |
| 게시글 수정 | 작성자만 | ADMIN도 타인 글 수정 불가 |
| 게시글 삭제 | 작성자/ADMIN | ADMIN은 모든 글 삭제 가능 |
| 댓글 조회 | ALL | 인증 불필요 |
| 댓글 작성 | GUEST/ADMIN | 로그인 필요 |
| 댓글 삭제 | 작성자/ADMIN | ADMIN은 모든 댓글 삭제 가능 |
| 회원 관리 | ADMIN | 관리자 전용 |

---

## ⚠️ 오류 처리

### HTTP 상태 코드

| 코드 | 의미 | 발생 상황 |
|------|------|----------|
| 200 | 성공 | 정상 처리 |
| 201 | 생성 성공 | 게시글/댓글 작성 |
| 400 | 잘못된 요청 | 입력값 검증 실패 |
| 401 | 인증 필요 | 토큰 없음/만료/잘못됨 |
| 403 | 권한 없음 | 접근 권한 부족 |
| 404 | 리소스 없음 | 게시글/댓글을 찾을 수 없음 |
| 500 | 서버 오류 | 내부 서버 오류 |

### 오류 응답 형식
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "아이디 또는 비밀번호가 올바르지 않습니다.",
    "details": "로그인 정보를 다시 확인해주세요."
  },
  "timestamp": "2025-12-18T17:15:00"
}
```

### 주요 오류 상황 및 해결방법

#### 401 Unauthorized
```json
{
  "success": false,
  "error": {
    "message": "JWT token is missing or invalid"
  }
}
```
**해결방법**: 로그인 후 발급받은 토큰을 Authorization 헤더에 포함

#### 403 Forbidden
```json
{
  "success": false,
  "error": {
    "message": "Access Denied"
  }
}
```
**해결방법**: 해당 API에 필요한 권한 확인 (ADMIN 권한 필요한 API인지 확인)

#### 400 Bad Request
```json
{
  "success": false,
  "error": {
    "message": "제목은 필수입니다"
  }
}
```
**해결방법**: 요청 데이터의 필수 필드 및 형식 확인

---

## 🛠️ 클라이언트 구현 예제

### JavaScript (Fetch API)

#### 로그인 및 토큰 저장
```javascript
async function login(userId, password) {
  const response = await fetch('/api/v1/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ userId, password })
  });
  
  const result = await response.json();
  
  if (result.success) {
    // 토큰 저장
    localStorage.setItem('accessToken', result.data.accessToken);
    localStorage.setItem('user', JSON.stringify(result.data.user));
    return result.data;
  } else {
    throw new Error(result.error.message);
  }
}
```

#### 인증이 필요한 API 호출
```javascript
async function createBoard(title, content) {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch('/api/v1/boards', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ title, content })
  });
  
  const result = await response.json();
  
  if (!result.success) {
    throw new Error(result.error.message);
  }
  
  return result.data;
}
```

### cURL 예제

#### 로그인
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "testuser01",
    "password": "password123"
  }'
```

#### 게시글 작성
```bash
curl -X POST http://localhost:8080/api/v1/boards \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "title": "API로 작성한 게시글",
    "content": "cURL을 사용해서 작성한 게시글입니다."
  }'
```

---

## 📊 응답 형식

### 성공 응답
```json
{
  "success": true,
  "data": { /* 실제 데이터 */ },
  "message": "요청이 성공적으로 처리되었습니다",
  "timestamp": "2025-12-18T17:15:00"
}
```

### 페이징 응답
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

## 🔧 개발 환경 설정

### 로컬 개발 시 CORS 설정
프론트엔드 개발 시 다른 포트에서 API 호출하는 경우, CORS 설정이 필요할 수 있습니다.

### API 테스트 도구
- **Swagger UI**: http://localhost:8080/swagger-ui.html (권장)
- **Postman**: REST API 테스트
- **cURL**: 명령줄 테스트
- **Insomnia**: API 테스트 도구

---

## 📞 지원 및 문의

### 기술 지원
- **Swagger 문서**: http://localhost:8080/swagger-ui.html
- **프로젝트 README**: /README.md
- **API 개발 계획서**: /REST_API_개발계획서.md

### 주요 특징
- **JWT 토큰 기반 인증**: Stateless 인증 방식
- **RESTful API 설계**: 표준 HTTP 메서드 사용
- **자동 API 문서화**: Swagger/OpenAPI 3.0
- **표준화된 응답 형식**: 일관된 JSON 응답
- **계층적 권한 체계**: GUEST/ADMIN 구분
- **입력값 검증**: Bean Validation 적용

---

**마지막 업데이트**: 2025-12-18  
**API 버전**: v1  
**문서 버전**: 1.0
