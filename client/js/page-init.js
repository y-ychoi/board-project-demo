/**
 * 페이지 공통 초기화 클래스
 */
class PageInitializer {
    /**
     * 페이지 초기화
     * @param {boolean} requireAuth - 인증 필요 여부 (기본값: true)
     * @param {boolean} adminOnly - 관리자 전용 여부 (기본값: false)
     * @returns {Object|null} - {app, user} 또는 null
     */
    static init(requireAuth = true, adminOnly = false) {
        const app = new BoardApp();

        // 인증 확인
        if (requireAuth && !app.isAuthenticated()) {
            window.location.href = 'login.html';
            return null;
        }

        let user = null;
        if (requireAuth) {
            user = app.getUser();

            // 관리자 전용 페이지 확인
            if (adminOnly && user.role !== 'ADMIN') {
                alert('관리자 권한이 필요합니다.');
                window.location.href = 'boards.html';
                return null;
            }

            // 사용자 정보 표시
            this.setupUserInfo(user);
        }

        // 공통 이벤트 리스너 설정
        this.setupCommonEvents(app);

        return { app, user };
    }

    /**
     * 사용자 정보 표시
     */
    static setupUserInfo(user) {
        const userInfoElement = document.getElementById('userInfo');
        if (userInfoElement) {
            userInfoElement.textContent = `${user.name}님 (${user.role})`;
        }
    }

    /**
     * 공통 이벤트 리스너 설정
     */
    static setupCommonEvents(app) {
        // 로그아웃 버튼
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => app.logout());
        }

        // 뒤로가기 버튼
        const backBtn = document.getElementById('backBtn');
        if (backBtn) {
            backBtn.addEventListener('click', () => window.location.href = 'boards.html');
        }

        // 취소 버튼
        const cancelBtn = document.getElementById('cancelBtn');
        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => window.history.back());
        }
    }

    /**
     * 관리자 메뉴 추가 (boards.html 전용)
     */
    static addAdminMenu(user) {
        if (user && user.role === 'ADMIN') {
            const userInfo = document.querySelector('.user-info');
            const logoutBtn = document.getElementById('logoutBtn');

            if (userInfo && logoutBtn) {
                const adminBtn = document.createElement('button');
                adminBtn.id = 'adminBtn';
                adminBtn.textContent = '회원 관리';
                adminBtn.className = 'btn-admin';
                adminBtn.onclick = () => window.location.href = 'admin.html';
                userInfo.insertBefore(adminBtn, logoutBtn);
            }
        }
    }
}