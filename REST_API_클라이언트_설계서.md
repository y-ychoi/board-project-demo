# REST API 클라이언트 설계서

**프로젝트**: board-project-demo  
**버전**: 1.0  
**작성일**: 2025-12-19

---

## 📋 개요

본 문서는 board-project-demo REST API를 위한 클라이언트 설계 가이드입니다. JWT 토큰 기반 인증, 표준화된 응답 형식, 계층적 권한 체계를 지원하는 클라이언트 구현 방안을 제시합니다.

---

## 🏗️ 클라이언트 아키텍처

### 핵심 설계 원칙
- **모듈화**: 인증, API 호출, 상태 관리 분리
- **재사용성**: 공통 로직의 모듈화
- **확장성**: 새로운 API 엔드포인트 쉽게 추가
- **오류 처리**: 표준화된 오류 응답 처리

### 클라이언트 구조
```
Client Application
├── Auth Module          # JWT 토큰 관리
├── API Module           # REST API 호출
├── State Module         # 애플리케이션 상태 관리
├── Error Handler        # 오류 처리
└── UI Components        # 사용자 인터페이스
```

---

## 🔐 인증 모듈 (Auth Module)

### 기능
- JWT 토큰 저장/관리
- 자동 토큰 갱신
- 로그인/로그아웃 처리

### JavaScript 구현 예시

```javascript
class AuthManager {
    constructor() {
        this.baseURL = 'http://localhost:8080/api/v1';
        this.tokenKey = 'accessToken';
        this.userKey = 'user';
    }

    // 로그인
    async login(userId, password) {
        const response = await fetch(`${this.baseURL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId, password })
        });

        const result = await response.json();
        
        if (result.success) {
            this.setToken(result.data.accessToken);
            this.setUser(result.data.user);
            return result.data;
        }
        throw new Error(result.error?.message || '로그인 실패');
    }

    // 회원가입
    async signup(userData) {
        const response = await fetch(`${this.baseURL}/auth/signup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const result = await response.json();
        
        if (!result.success) {
            throw new Error(result.error?.message || '회원가입 실패');
        }
        return result.data;
    }

    // 토큰 저장
    setToken(token) {
        localStorage.setItem(this.tokenKey, token);
    }

    // 토큰 조회
    getToken() {
        return localStorage.getItem(this.tokenKey);
    }

    // 사용자 정보 저장
    setUser(user) {
        localStorage.setItem(this.userKey, JSON.stringify(user));
    }

    // 사용자 정보 조회
    getUser() {
        const user = localStorage.getItem(this.userKey);
        return user ? JSON.parse(user) : null;
    }

    // 로그아웃
    logout() {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
    }

    // 로그인 상태 확인
    isAuthenticated() {
        return !!this.getToken();
    }

    // 권한 확인
    hasRole(role) {
        const user = this.getUser();
        return user?.role === role;
    }

    // 관리자 권한 확인
    isAdmin() {
        return this.hasRole('ADMIN');
    }
}
```

---

## 🌐 API 모듈 (API Module)

### 기능
- REST API 호출 래퍼
- 자동 인증 헤더 추가
- 응답 데이터 표준화
- 오류 처리

### JavaScript 구현 예시

```javascript
class ApiClient {
    constructor(authManager) {
        this.baseURL = 'http://localhost:8080/api/v1';
        this.auth = authManager;
    }

    // 공통 요청 메서드
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        // 인증 토큰 자동 추가
        const token = this.auth.getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        try {
            const response = await fetch(url, config);
            const result = await response.json();

            if (!result.success) {
                throw new ApiError(result.error, response.status);
            }

            return result.data;
        } catch (error) {
            if (error instanceof ApiError) {
                throw error;
            }
            throw new ApiError({ message: '네트워크 오류' }, 0);
        }
    }

    // GET 요청
    async get(endpoint, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const url = queryString ? `${endpoint}?${queryString}` : endpoint;
        return this.request(url, { method: 'GET' });
    }

    // POST 요청
    async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    // PUT 요청
    async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    // DELETE 요청
    async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
}

// API 오류 클래스
class ApiError extends Error {
    constructor(error, status) {
        super(error.message);
        this.code = error.code;
        this.details = error.details;
        this.status = status;
    }
}
```

---

## 📝 게시판 서비스 (Board Service)

### JavaScript 구현 예시

```javascript
class BoardService {
    constructor(apiClient) {
        this.api = apiClient;
    }

    // 게시글 목록 조회
    async getBoards(page = 0, size = 10) {
        return this.api.get('/boards', { page, size });
    }

    // 게시글 상세 조회
    async getBoard(boardNo) {
        return this.api.get(`/boards/${boardNo}`);
    }

    // 게시글 작성
    async createBoard(boardData) {
        return this.api.post('/boards', boardData);
    }

    // 게시글 수정
    async updateBoard(boardNo, boardData) {
        return this.api.put(`/boards/${boardNo}`, boardData);
    }

    // 게시글 삭제
    async deleteBoard(boardNo) {
        return this.api.delete(`/boards/${boardNo}`);
    }

    // 댓글 목록 조회
    async getComments(boardNo) {
        return this.api.get(`/boards/${boardNo}/comments`);
    }

    // 댓글 작성
    async createComment(boardNo, content) {
        return this.api.post(`/boards/${boardNo}/comments`, { content });
    }

    // 댓글 삭제
    async deleteComment(boardNo, commentNo) {
        return this.api.delete(`/boards/${boardNo}/comments/${commentNo}`);
    }
}
```

---

## 👥 사용자 서비스 (User Service)

### JavaScript 구현 예시

```javascript
class UserService {
    constructor(apiClient) {
        this.api = apiClient;
    }

    // 회원 목록 조회 (ADMIN만)
    async getUsers() {
        return this.api.get('/users');
    }

    // 내 정보 조회
    async getMyInfo() {
        return this.api.get('/users/me');
    }

    // 사용자 권한 변경 (ADMIN만)
    async updateUserRole(userNo, role) {
        return this.api.put(`/users/${userNo}/role`, { role });
    }
}
```

---

## 🎯 상태 관리 모듈 (State Module)

### 기능
- 애플리케이션 전역 상태 관리
- 사용자 정보, 게시글 목록 등 캐싱
- 상태 변경 알림

### JavaScript 구현 예시

```javascript
class StateManager {
    constructor() {
        this.state = {
            user: null,
            boards: [],
            currentBoard: null,
            loading: false,
            error: null
        };
        this.listeners = [];
    }

    // 상태 업데이트
    setState(newState) {
        this.state = { ...this.state, ...newState };
        this.notifyListeners();
    }

    // 상태 조회
    getState() {
        return this.state;
    }

    // 리스너 등록
    subscribe(listener) {
        this.listeners.push(listener);
        return () => {
            this.listeners = this.listeners.filter(l => l !== listener);
        };
    }

    // 리스너 알림
    notifyListeners() {
        this.listeners.forEach(listener => listener(this.state));
    }

    // 로딩 상태 설정
    setLoading(loading) {
        this.setState({ loading });
    }

    // 오류 상태 설정
    setError(error) {
        this.setState({ error });
    }

    // 사용자 정보 설정
    setUser(user) {
        this.setState({ user });
    }

    // 게시글 목록 설정
    setBoards(boards) {
        this.setState({ boards });
    }

    // 현재 게시글 설정
    setCurrentBoard(board) {
        this.setState({ currentBoard: board });
    }
}
```

---

## ⚠️ 오류 처리 모듈 (Error Handler)

### JavaScript 구현 예시

```javascript
class ErrorHandler {
    constructor(stateManager) {
        this.state = stateManager;
    }

    // 오류 처리
    handleError(error) {
        console.error('API Error:', error);

        let message = '알 수 없는 오류가 발생했습니다.';

        if (error instanceof ApiError) {
            switch (error.status) {
                case 400:
                    message = error.message || '잘못된 요청입니다.';
                    break;
                case 401:
                    message = '로그인이 필요합니다.';
                    this.handleUnauthorized();
                    break;
                case 403:
                    message = '권한이 없습니다.';
                    break;
                case 404:
                    message = '요청한 리소스를 찾을 수 없습니다.';
                    break;
                case 500:
                    message = '서버 오류가 발생했습니다.';
                    break;
                default:
                    message = error.message || message;
            }
        }

        this.state.setError(message);
        this.showErrorMessage(message);
    }

    // 인증 오류 처리
    handleUnauthorized() {
        // 토큰 제거 및 로그인 페이지로 이동
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
    }

    // 오류 메시지 표시
    showErrorMessage(message) {
        // UI에 오류 메시지 표시 (예: 토스트, 알림 등)
        alert(message); // 간단한 예시
    }

    // 오류 상태 초기화
    clearError() {
        this.state.setError(null);
    }
}
```

---

## 🎨 UI 컴포넌트 예시

### React 컴포넌트 예시

```jsx
// 게시글 목록 컴포넌트
function BoardList({ boardService, stateManager, errorHandler }) {
    const [boards, setBoards] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(0);

    useEffect(() => {
        loadBoards();
    }, [page]);

    const loadBoards = async () => {
        try {
            setLoading(true);
            const data = await boardService.getBoards(page, 10);
            setBoards(data.content);
        } catch (error) {
            errorHandler.handleError(error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>로딩 중...</div>;

    return (
        <div>
            <h2>게시글 목록</h2>
            {boards.map(board => (
                <div key={board.boardNo} className="board-item">
                    <h3>{board.title}</h3>
                    <p>작성자: {board.authorName}</p>
                    <p>작성일: {board.createDt}</p>
                </div>
            ))}
            <Pagination 
                page={page} 
                onPageChange={setPage}
                totalPages={boards.totalPages}
            />
        </div>
    );
}

// 로그인 컴포넌트
function LoginForm({ authManager, errorHandler }) {
    const [formData, setFormData] = useState({
        userId: '',
        password: ''
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await authManager.login(formData.userId, formData.password);
            window.location.href = '/boards';
        } catch (error) {
            errorHandler.handleError(error);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <input
                type="text"
                placeholder="아이디"
                value={formData.userId}
                onChange={(e) => setFormData({...formData, userId: e.target.value})}
                required
            />
            <input
                type="password"
                placeholder="비밀번호"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                required
            />
            <button type="submit">로그인</button>
        </form>
    );
}
```

---

## 📱 모바일 앱 클라이언트 (React Native)

### 인증 관리

```javascript
// AsyncStorage를 사용한 토큰 관리
import AsyncStorage from '@react-native-async-storage/async-storage';

class MobileAuthManager {
    constructor() {
        this.baseURL = 'http://localhost:8080/api/v1';
        this.tokenKey = 'accessToken';
        this.userKey = 'user';
    }

    async setToken(token) {
        await AsyncStorage.setItem(this.tokenKey, token);
    }

    async getToken() {
        return await AsyncStorage.getItem(this.tokenKey);
    }

    async setUser(user) {
        await AsyncStorage.setItem(this.userKey, JSON.stringify(user));
    }

    async getUser() {
        const user = await AsyncStorage.getItem(this.userKey);
        return user ? JSON.parse(user) : null;
    }

    async logout() {
        await AsyncStorage.multiRemove([this.tokenKey, this.userKey]);
    }

    async login(userId, password) {
        const response = await fetch(`${this.baseURL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId, password })
        });

        const result = await response.json();
        
        if (result.success) {
            await this.setToken(result.data.accessToken);
            await this.setUser(result.data.user);
            return result.data;
        }
        throw new Error(result.error?.message || '로그인 실패');
    }
}
```

---

## 🔧 클라이언트 초기화 및 설정

### 메인 애플리케이션 클래스

```javascript
class BoardApp {
    constructor() {
        this.authManager = new AuthManager();
        this.apiClient = new ApiClient(this.authManager);
        this.stateManager = new StateManager();
        this.errorHandler = new ErrorHandler(this.stateManager);
        
        this.boardService = new BoardService(this.apiClient);
        this.userService = new UserService(this.apiClient);
        
        this.init();
    }

    async init() {
        // 저장된 사용자 정보 복원
        const user = this.authManager.getUser();
        if (user) {
            this.stateManager.setUser(user);
        }

        // 전역 오류 처리기 설정
        window.addEventListener('unhandledrejection', (event) => {
            this.errorHandler.handleError(event.reason);
        });
    }

    // 앱 시작
    start() {
        // 라우팅 설정 및 초기 화면 렌더링
        this.setupRouting();
        this.render();
    }

    setupRouting() {
        // 클라이언트 사이드 라우팅 설정
        // (React Router, Vue Router 등 사용)
    }

    render() {
        // 초기 UI 렌더링
    }
}

// 앱 시작
const app = new BoardApp();
app.start();
```

---

## 📊 데이터 모델

### TypeScript 인터페이스 정의

```typescript
// 사용자 모델
interface User {
    userNo: number;
    userId: string;
    name: string;
    email: string;
    role: 'GUEST' | 'ADMIN';
    createDt: string;
    modifyDt: string;
}

// 게시글 모델
interface Board {
    boardNo: number;
    title: string;
    content: string;
    viewCnt: number;
    authorNo: number;
    authorName: string;
    createDt: string;
    modifyDt: string;
}

// 댓글 모델
interface Comment {
    commentNo: number;
    content: string;
    boardNo: number;
    authorNo: number;
    authorName: string;
    createDt: string;
    modifyDt: string;
}

// API 응답 모델
interface ApiResponse<T> {
    success: boolean;
    data?: T;
    message?: string;
    error?: {
        code: string;
        message: string;
        details: string;
    };
    timestamp: string;
}

// 페이징 응답 모델
interface PageResponse<T> {
    content: T[];
    pageable: {
        page: number;
        size: number;
        totalElements: number;
        totalPages: number;
        first: boolean;
        last: boolean;
    };
}
```

---

## 🧪 테스트 전략

### 단위 테스트 예시 (Jest)

```javascript
// AuthManager 테스트
describe('AuthManager', () => {
    let authManager;

    beforeEach(() => {
        authManager = new AuthManager();
        localStorage.clear();
    });

    test('로그인 성공 시 토큰과 사용자 정보 저장', async () => {
        // Mock fetch
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve({
                    success: true,
                    data: {
                        accessToken: 'test-token',
                        user: { userNo: 1, userId: 'test' }
                    }
                })
            })
        );

        const result = await authManager.login('test', 'password');

        expect(result.accessToken).toBe('test-token');
        expect(localStorage.getItem('accessToken')).toBe('test-token');
    });

    test('로그인 실패 시 오류 발생', async () => {
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve({
                    success: false,
                    error: { message: '로그인 실패' }
                })
            })
        );

        await expect(authManager.login('test', 'wrong')).rejects.toThrow('로그인 실패');
    });
});
```

---

## 🚀 배포 및 빌드

### Webpack 설정 예시

```javascript
// webpack.config.js
module.exports = {
    entry: './src/index.js',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'bundle.js'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: 'babel-loader'
            }
        ]
    },
    devServer: {
        proxy: {
            '/api': 'http://localhost:8080'
        }
    }
};
```

### 환경 설정

```javascript
// config.js
const config = {
    development: {
        apiBaseURL: 'http://localhost:8080/api/v1',
        enableLogging: true
    },
    production: {
        apiBaseURL: 'https://api.yourapp.com/v1',
        enableLogging: false
    }
};

export default config[process.env.NODE_ENV || 'development'];
```

---

## 📈 성능 최적화

### 캐싱 전략

```javascript
class CacheManager {
    constructor() {
        this.cache = new Map();
        this.ttl = 5 * 60 * 1000; // 5분
    }

    set(key, data) {
        this.cache.set(key, {
            data,
            timestamp: Date.now()
        });
    }

    get(key) {
        const item = this.cache.get(key);
        if (!item) return null;

        if (Date.now() - item.timestamp > this.ttl) {
            this.cache.delete(key);
            return null;
        }

        return item.data;
    }

    clear() {
        this.cache.clear();
    }
}
```

### 요청 최적화

```javascript
class OptimizedApiClient extends ApiClient {
    constructor(authManager) {
        super(authManager);
        this.cache = new CacheManager();
        this.pendingRequests = new Map();
    }

    async get(endpoint, params = {}) {
        const cacheKey = `${endpoint}?${new URLSearchParams(params)}`;
        
        // 캐시된 데이터 확인
        const cached = this.cache.get(cacheKey);
        if (cached) return cached;

        // 중복 요청 방지
        if (this.pendingRequests.has(cacheKey)) {
            return this.pendingRequests.get(cacheKey);
        }

        const request = super.get(endpoint, params);
        this.pendingRequests.set(cacheKey, request);

        try {
            const result = await request;
            this.cache.set(cacheKey, result);
            return result;
        } finally {
            this.pendingRequests.delete(cacheKey);
        }
    }
}
```

---

## 🔒 보안 고려사항

### XSS 방지

```javascript
class SecurityUtils {
    static sanitizeHtml(html) {
        const div = document.createElement('div');
        div.textContent = html;
        return div.innerHTML;
    }

    static validateInput(input, type) {
        switch (type) {
            case 'email':
                return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(input);
            case 'userId':
                return /^[a-zA-Z0-9_]{4,20}$/.test(input);
            default:
                return input.length > 0;
        }
    }
}
```

### CSRF 방지

```javascript
// API 요청 시 CSRF 토큰 자동 추가
class SecureApiClient extends ApiClient {
    async request(endpoint, options = {}) {
        // CSRF 토큰 추가 (필요한 경우)
        const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content;
        if (csrfToken) {
            options.headers = {
                ...options.headers,
                'X-CSRF-TOKEN': csrfToken
            };
        }

        return super.request(endpoint, options);
    }
}
```

---

## 📚 사용 예시

### 완전한 게시글 작성 플로우

```javascript
async function createBoardExample() {
    const app = new BoardApp();
    
    try {
        // 1. 로그인
        await app.authManager.login('testuser', 'password123');
        
        // 2. 게시글 작성
        const boardData = {
            title: '새 게시글',
            content: '게시글 내용입니다.'
        };
        
        const newBoard = await app.boardService.createBoard(boardData);
        console.log('게시글 작성 완료:', newBoard);
        
        // 3. 댓글 작성
        const comment = await app.boardService.createComment(
            newBoard.boardNo, 
            '첫 번째 댓글입니다!'
        );
        console.log('댓글 작성 완료:', comment);
        
    } catch (error) {
        app.errorHandler.handleError(error);
    }
}
```

---

## 🎯 결론

본 설계서는 board-project-demo REST API를 위한 완전한 클라이언트 구현 가이드를 제공합니다. 모듈화된 구조, 표준화된 오류 처리, 보안 고려사항을 포함하여 확장 가능하고 유지보수가 용이한 클라이언트를 구축할 수 있습니다.

### 주요 특징
- **JWT 토큰 기반 인증** 완전 지원
- **모듈화된 아키텍처**로 재사용성 극대화
- **표준화된 오류 처리**로 안정성 확보
- **다양한 플랫폼** 지원 (웹, 모바일)
- **성능 최적화** 기법 적용
- **보안 고려사항** 반영

---

**마지막 업데이트**: 2025-12-19  
**문서 버전**: 1.0
