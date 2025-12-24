/**
 * JWT 토큰 기반 인증 관리 클래스
 */
class AuthManager {
    constructor() {
        this.baseURL = 'http://localhost:8080/api/v1';
        this.tokenKey = 'accessToken';
        this.userKey = 'user';
    }

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

    setToken(token) {
        localStorage.setItem(this.tokenKey, token);
    }

    getToken() {
        return localStorage.getItem(this.tokenKey);
    }

    setUser(user) {
        localStorage.setItem(this.userKey, JSON.stringify(user));
    }

    getUser() {
        const user = localStorage.getItem(this.userKey);
        return user ? JSON.parse(user) : null;
    }

    logout() {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
    }

    isAuthenticated() {
        return !!this.getToken();
    }

    hasRole(role) {
        const user = this.getUser();
        return user?.role === role;
    }

    isAdmin() {
        return this.hasRole('ADMIN');
    }
}
