# REST API 구조 및 플로우 가이드

**프로젝트:** board-project-demo  
**작성일:** 2025-12-16  
**현재 상태:** Phase 3 완료 (인증 + 사용자 관리 API)

---

## 📊 현재까지 완성된 API 구조

### 🔐 **인증 API** (`/api/v1/auth`)

| Method | Endpoint | 설명 | 권한 | 상태 |
|--------|----------|------|------|------|
| POST | `/api/v1/auth/signup` | 회원가입 | 모든 사용자 | ✅ |
| POST | `/api/v1/auth/login` | 로그인 (JWT 토큰 발급) | 모든 사용자 | ✅ |

### 👥 **사용자 관리 API** (`/api/v1/users`)

| Method | Endpoint | 설명 | 권한 | 상태 |
|--------|----------|------|------|------|
| GET | `/api/v1/users` | 회원 목록 조회 | ADMIN만 | ✅ |
| PUT | `/api/v1/users/{id}/role` | 권한 변경 | ADMIN만 | ✅ |
| GET | `/api/v1/users/me` | 내 정보 조회 | 로그인 사용자 | ✅ |

### 📝 **게시판 API** (`/api/v1/boards`) - 예정

| Method | Endpoint | 설명 | 권한 | 상태 |
|--------|----------|------|------|------|
| GET | `/api/v1/boards` | 게시글 목록 (페이징) | 모든 사용자 | ⏳ |
| GET | `/api/v1/boards/{id}` | 게시글 상세 조회 | 모든 사용자 | ⏳ |
| POST | `/api/v1/boards` | 게시글 작성 | 로그인 사용자 | ⏳ |
| PUT | `/api/v1/boards/{id}` | 게시글 수정 | 작성자만 | ⏳ |
| DELETE | `/api/v1/boards/{id}` | 게시글 삭제 | 작성자/ADMIN | ⏳ |

### 💬 **댓글 API** (`/api/v1/comments`) - 예정

| Method | Endpoint | 설명 | 권한 | 상태 |
|--------|----------|------|------|------|
| GET | `/api/v1/boards/{id}/comments` | 댓글 목록 | 모든 사용자 | ⏳ |
| POST | `/api/v1/boards/{id}/comments` | 댓글 작성 | 로그인 사용자 | ⏳ |
| DELETE | `/api/v1/comments/{id}` | 댓글 삭제 | 작성자/ADMIN | ⏳ |

---

## 🔄 API 진행 플로우 시나리오

### **시나리오 1: 새 사용자 가입 및 정보 조회**

```
1️⃣ 회원가입
POST /api/v1/auth/signup
{
  "userId": "newuser01",
  "password": "password123",
  "passwordConfirm": "password123", 
  "name": "홍길동",
  "email": "user@example.com"
}
↓
Response: 201 Created
{
  "success": true,
  "message": "회원가입이 성공적으로 완료되었습니다."
}

2️⃣ 로그인
POST /api/v1/auth/login
{
  "userId": "newuser01",
  "password": "password123"
}
↓
Response: 200 OK
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userNo": 2,
      "userId": "newuser01",
      "name": "홍길동",
      "email": "user@example.com",
      "role": "GUEST"
    }
  }
}

3️⃣ 내 정보 조회
GET /api/v1/users/me
Headers: Authorization: Bearer {accessToken}
↓
Response: 200 OK
{
  "success": true,
  "data": {
    "userNo": 2,
    "userId": "newuser01",
    "name": "홍길동",
    "email": "user@example.com",
    "role": "GUEST",
    "createDt": "2025-12-16T13:30:00",
    "modifyDt": "2025-12-16T13:30:00"
  }
}
```

### **시나리오 2: 관리자의 회원 관리**

```
1️⃣ 관리자 로그인
POST /api/v1/auth/login
{
  "userId": "admin01",
  "password": "admin123"
}
↓
Response: JWT 토큰 (role: ADMIN)

2️⃣ 회원 목록 조회
GET /api/v1/users
Headers: Authorization: Bearer {admin_token}
↓
Response: 200 OK
{
  "success": true,
  "data": [
    {
      "userNo": 1,
      "userId": "admin01",
      "name": "관리자",
      "email": "admin@example.com", 
      "role": "ADMIN",
      "createDt": "2025-12-15T10:00:00"
    },
    {
      "userNo": 2,
      "userId": "newuser01",
      "name": "홍길동",
      "email": "user@example.com",
      "role": "GUEST", 
      "createDt": "2025-12-16T13:30:00"
    }
  ]
}

3️⃣ 사용자 권한 변경
PUT /api/v1/users/2/role
Headers: Authorization: Bearer {admin_token}
{
  "role": "ADMIN"
}
↓
Response: 200 OK
{
  "success": true,
  "message": "권한이 성공적으로 변경되었습니다."
}
```

### **시나리오 3: 권한 부족 시 오류 처리**

```
1️⃣ GUEST 사용자가 관리자 API 호출
GET /api/v1/users
Headers: Authorization: Bearer {guest_token}
↓
Response: 403 Forbidden
{
  "success": false,
  "error": {
    "code": "ACCESS_DENIED",
    "message": "접근 권한이 없습니다.",
    "details": "해당 기능을 사용할 권한이 없습니다."
  },
  "timestamp": "2025-12-16T13:50:00"
}

2️⃣ 토큰 없이 API 호출
GET /api/v1/users/me
(Authorization 헤더 없음)
↓
Response: 401 Unauthorized
{
  "success": false,
  "error": {
    "code": "AUTHENTICATION_FAILED",
    "message": "인증에 실패했습니다.",
    "details": "JWT 토큰이 필요합니다."
  }
}
```

---

## 🏗️ API 아키텍처 구조

### **전체 시스템 아키텍처**

```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                         │
│                                                         │
│  📱 Mobile App    🌐 Web SPA    🔧 Postman/Swagger     │
│                                                         │
│  HTTP/JSON 요청 (Authorization: Bearer {token})        │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                 REST Controller Layer                   │
│                                                         │
│  🔐 AuthRestController     👥 UserRestController        │
│  ├─ POST /auth/signup      ├─ GET /users               │
│  └─ POST /auth/login       ├─ PUT /users/{id}/role     │
│                            └─ GET /users/me            │
│                                                         │
│  📝 BoardRestController (예정)  💬 CommentRestController │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                   Security Layer                        │
│                                                         │
│  🛡️ RestSecurityConfig    🔑 JwtAuthenticationFilter   │
│  ├─ URL 권한 설정          ├─ JWT 토큰 추출             │
│  ├─ Stateless 인증        ├─ 토큰 유효성 검증          │
│  └─ @Order(1) 우선적용    └─ Security Context 설정     │
│                                                         │
│  ⚠️ RestExceptionHandler (전역 예외 처리)               │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                   Service Layer                         │
│                                                         │
│  👤 UserService (기존 MVC + REST API 확장)             │
│  ├─ create(SignupRequestDto) - REST API용              │
│  ├─ getUserByUserId() - JWT 인증용                     │
│  ├─ getAllUsersForApi() - 회원 목록 API용              │
│  └─ updateUserRole() - 권한 변경용                     │
│                                                         │
│  📝 BoardService (예정)    💬 CommentService (예정)     │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                Repository & Database                    │
│                                                         │
│  🗃️ UserRepository        🗄️ MySQL Database            │
│  ├─ findByUserId()        ├─ TB_USER                   │
│  ├─ findAllByOrder...()   ├─ TB_BOARD (기존)           │
│  └─ save()                └─ TB_COMMENT (기존)         │
│                                                         │
│  📝 BoardRepository       💬 CommentRepository          │
└─────────────────────────────────────────────────────────┘
```

### **데이터 플로우 (요청 → 응답)**

```
1️⃣ Client Request
   📤 HTTP Request (JSON + JWT Token)
   
2️⃣ Security Filter
   🔍 JwtAuthenticationFilter
   ├─ Authorization 헤더에서 토큰 추출
   ├─ 토큰 유효성 검증 (만료, 서명 체크)
   ├─ 토큰에서 사용자 정보 추출
   └─ SecurityContext에 인증 정보 설정
   
3️⃣ REST Controller
   🎯 @PreAuthorize 권한 검증
   ├─ hasRole('ADMIN') - 관리자 권한 체크
   ├─ isAuthenticated() - 로그인 여부 체크
   └─ 요청 데이터 검증 (@Valid)
   
4️⃣ Service Layer
   ⚙️ 비즈니스 로직 처리
   ├─ @Transactional 트랜잭션 관리
   ├─ 데이터 검증 및 변환
   └─ Repository 호출
   
5️⃣ Repository & Database
   🗄️ 데이터 접근 및 저장
   ├─ JPA 쿼리 실행
   ├─ Entity 조회/저장/수정/삭제
   └─ 결과 반환
   
6️⃣ Response Processing
   📦 DTO 변환 및 응답 생성
   ├─ Entity → ResponseDTO 변환
   ├─ ApiResponseDto로 표준화
   └─ JSON 직렬화
   
7️⃣ Client Response
   📥 HTTP Response (JSON)
   ├─ 성공: 200/201 + 데이터
   └─ 실패: 400/401/403/500 + 오류 정보
```

---

## 🔒 보안 및 권한 체계

### **JWT 토큰 구조**

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "admin01",           // 사용자 ID
    "auth": "ROLE_ADMIN",       // 권한 정보
    "iat": 1702641600,          // 발급 시간
    "exp": 1702645200           // 만료 시간
  },
  "signature": "..."
}
```

### **권한별 접근 제어**

```
🌐 모든 사용자 (비로그인 포함)
├─ POST /api/v1/auth/signup     (회원가입)
├─ POST /api/v1/auth/login      (로그인)
├─ GET  /api/v1/boards          (게시글 목록 - 예정)
└─ GET  /api/v1/boards/{id}     (게시글 상세 - 예정)

🔐 로그인한 사용자 (GUEST + ADMIN)
├─ GET  /api/v1/users/me        (내 정보 조회)
├─ POST /api/v1/boards          (게시글 작성 - 예정)
├─ PUT  /api/v1/boards/{id}     (본인 게시글 수정 - 예정)
└─ POST /api/v1/comments        (댓글 작성 - 예정)

👑 ADMIN 전용
├─ GET  /api/v1/users           (회원 목록 조회)
├─ PUT  /api/v1/users/{id}/role (권한 변경)
└─ DELETE /api/v1/boards/{id}   (모든 게시글 삭제 - 예정)
```

### **보안 특징**

- ✅ **Stateless 인증**: 세션 없이 JWT 토큰만 사용
- ✅ **토큰 만료**: Access Token 1시간, Refresh Token 7일
- ✅ **메서드 레벨 보안**: `@PreAuthorize`로 세밀한 권한 제어
- ✅ **자기 보호**: 본인 권한 변경 방지
- ✅ **전역 예외 처리**: 일관된 오류 응답 형식
- ✅ **입력값 검증**: `@Valid`로 데이터 무결성 보장

---

## 📱 클라이언트 사용 가이드

### **인증 헤더 설정**

```javascript
// JavaScript 예시
const token = localStorage.getItem('accessToken');

fetch('/api/v1/users/me', {
    method: 'GET',
    headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    }
});
```

### **오류 처리**

```javascript
// 응답 처리 예시
fetch('/api/v1/users', {
    headers: { 'Authorization': `Bearer ${token}` }
})
.then(response => {
    if (response.status === 401) {
        // 토큰 만료 → 로그인 페이지로 이동
        window.location.href = '/login';
    } else if (response.status === 403) {
        // 권한 부족 → 오류 메시지 표시
        alert('접근 권한이 없습니다.');
    }
    return response.json();
})
.then(data => {
    if (data.success) {
        // 성공 처리
        console.log(data.data);
    } else {
        // 오류 처리
        console.error(data.error.message);
    }
});
```

---

## 🚀 향후 확장 계획

### **Phase 4: 게시판 API** (다음 단계)
- 게시글 CRUD API 구현
- 페이징 처리
- 작성자 권한 검증

### **Phase 5: 댓글 API**
- 댓글 CRUD API 구현
- 게시글별 댓글 조회

### **Phase 6: 문서화 및 테스트**
- Swagger 어노테이션 추가
- API 문서 완성
- 통합 테스트

### **추가 기능 (선택사항)**
- 파일 업로드 API
- 검색 API
- 알림 API
- 통계 API

---

**작성일:** 2025-12-16  
**현재 진행률:** Phase 3/6 완료 (50%)  
**다음 작업:** Phase 4 - 게시판 API 구현
