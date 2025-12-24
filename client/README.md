# Board Project REST API 클라이언트

이 클라이언트는 board-project-demo REST API를 사용하는 웹 애플리케이션입니다.

## 📁 파일 구조

```
client/
├── js/
│   ├── auth.js              # JWT 인증 관리
│   ├── api.js               # REST API 호출
│   ├── board-service.js     # 게시판 서비스
│   ├── user-service.js      # 사용자 서비스
│   ├── state.js             # 상태 관리
│   ├── error-handler.js     # 오류 처리
│   └── app.js               # 메인 애플리케이션
├── css/
│   └── style.css            # 스타일시트
├── login.html               # 로그인 페이지
├── signup.html              # 회원가입 페이지
├── boards.html              # 게시글 목록
├── board-create.html        # 게시글 작성
├── board-detail.html        # 게시글 상세
└── README.md                # 이 파일
```

## 🚀 사용 방법

### 1. 서버 실행
먼저 Spring Boot 서버를 실행해야 합니다:
```bash
cd /mnt/c/workspace/board-project-demo
./mvnw spring-boot:run
```

### 2. 클라이언트 실행
웹 서버를 통해 클라이언트를 실행하거나, 간단히 HTML 파일을 브라우저에서 직접 열 수 있습니다.

**Python 웹 서버 사용 (권장):**
```bash
cd client
python -m http.server 8000
```
그 후 http://localhost:8000/login.html 접속

### 3. 기본 사용 흐름
1. `login.html`에서 로그인 또는 회원가입
2. `boards.html`에서 게시글 목록 확인
3. 게시글 작성, 수정, 삭제
4. 댓글 작성 및 삭제

## 🔧 주요 기능

### 인증 (AuthManager)
- JWT 토큰 기반 로그인/로그아웃
- 자동 토큰 저장 및 관리
- 권한 확인 (GUEST/ADMIN)

### API 호출 (ApiClient)
- REST API 호출 래퍼
- 자동 인증 헤더 추가
- 표준화된 오류 처리

### 게시판 (BoardService)
- 게시글 CRUD 작업
- 댓글 관리
- 페이징 처리

### 상태 관리 (StateManager)
- 애플리케이션 전역 상태
- 리액티브 상태 업데이트
- 로딩/오류 상태 관리

## 🎯 API 엔드포인트

### 인증
- `POST /api/v1/auth/login` - 로그인
- `POST /api/v1/auth/signup` - 회원가입

### 게시판
- `GET /api/v1/boards` - 게시글 목록
- `GET /api/v1/boards/{id}` - 게시글 상세
- `POST /api/v1/boards` - 게시글 작성
- `PUT /api/v1/boards/{id}` - 게시글 수정
- `DELETE /api/v1/boards/{id}` - 게시글 삭제

### 댓글
- `GET /api/v1/boards/{id}/comments` - 댓글 목록
- `POST /api/v1/boards/{id}/comments` - 댓글 작성
- `DELETE /api/v1/boards/{boardId}/comments/{commentId}` - 댓글 삭제

## 💡 사용 예시

### JavaScript에서 직접 사용
```javascript
// 앱 초기화
const app = new BoardApp();

// 로그인
try {
    await app.login('testuser', 'password123');
    console.log('로그인 성공');
} catch (error) {
    console.error('로그인 실패:', error.message);
}

// 게시글 목록 조회
const boards = await app.loadBoards(0, 10);
console.log('게시글 목록:', boards);

// 게시글 작성
const newBoard = await app.createBoard({
    title: '새 게시글',
    content: '게시글 내용'
});
```

## 🔒 보안 고려사항

- JWT 토큰은 localStorage에 저장
- 모든 API 요청에 자동으로 Authorization 헤더 추가
- 401 오류 시 자동 로그아웃 처리
- XSS 방지를 위한 입력값 검증

## 🐛 문제 해결

### CORS 오류
서버에서 CORS 설정이 필요할 수 있습니다. Spring Boot 서버의 CORS 설정을 확인하세요.

### 토큰 만료
토큰이 만료되면 자동으로 로그인 페이지로 리다이렉트됩니다.

### 네트워크 오류
서버가 실행 중인지 확인하고, API URL이 올바른지 확인하세요.

## 📱 모바일 지원

현재 웹 브라우저용으로 설계되었지만, 반응형 디자인을 적용하여 모바일에서도 사용 가능합니다.

## 🔄 확장 가능성

- React/Vue.js 컴포넌트로 변환 가능
- React Native로 모바일 앱 개발 가능
- 추가 API 엔드포인트 쉽게 연동 가능
