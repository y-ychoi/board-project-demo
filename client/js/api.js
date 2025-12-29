/**
 * API 오류 클래스
 */
class ApiError extends Error {
    constructor(error, status) {
        super(error.message);
        this.code = error.code;
        this.details = error.details;
        this.status = status;
    }
}

/**
 * REST API 호출 클라이언트
 */
class ApiClient {
    constructor(authManager) {
        this.baseURL = 'http://localhost:8080/api/v1';
        this.auth = authManager;
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

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

    async get(endpoint, params = {}) {
        // 캐시 방지를 위한 timestamp 추가
        params._t = Date.now();
        const queryString = new URLSearchParams(params).toString();
        const url = queryString ? `${endpoint}?${queryString}` : endpoint;
        return this.request(url, { method: 'GET' });
    }

    async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
}
