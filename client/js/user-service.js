/**
 * 사용자 서비스 클래스
 */
class UserService {
    constructor(apiClient) {
        this.api = apiClient;
    }

    async getUsers() {
        return this.api.get('/users');
    }

    async getMyInfo() {
        return this.api.get('/users/me');
    }

    async updateUserRole(userNo, role) {
        return this.api.put(`/users/${userNo}/role`, { role });
    }
}
