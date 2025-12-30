/**
 * 오류 처리 클래스
 */
class ErrorHandler {
    constructor(stateManager) {
        this.state = stateManager;
    }

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
    } else if (error.message) {
        // 일반 Error 객체의 메시지도 표시
        message = error.message;
    }

    this.state.setError(message);
    this.showErrorMessage(message);
}

    handleUnauthorized() {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
        window.location.href = 'login.html';
    }

    showErrorMessage(message) {
        const errorDiv = document.getElementById('error-message');
        if (errorDiv) {
            errorDiv.textContent = message;
            errorDiv.style.display = 'block';
            setTimeout(() => {
                errorDiv.style.display = 'none';
            }, 5000);
        } else {
            alert(message);
        }
    }

    clearError() {
        this.state.setError(null);
        const errorDiv = document.getElementById('error-message');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
    }
}
