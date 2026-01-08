/**
 * 전역 에러 처리 시스템
 */
class GlobalErrorHandler {
    static instance = null;

    constructor(stateManager = null) {
        if (GlobalErrorHandler.instance) {
            if (stateManager && !GlobalErrorHandler.instance.state) {
                GlobalErrorHandler.instance.state = stateManager;
            }
            return GlobalErrorHandler.instance;
        }

        this.state = stateManager;
        this.setupGlobalHandlers();
        GlobalErrorHandler.instance = this;
    }

    static getInstance(stateManager = null) {
        if (!GlobalErrorHandler.instance) {
            new GlobalErrorHandler(stateManager);
        } else if (stateManager && !GlobalErrorHandler.instance.state) {
            GlobalErrorHandler.instance.state = stateManager;
        }
        return GlobalErrorHandler.instance;
    }

    setupGlobalHandlers() {
        window.addEventListener('unhandledrejection', (event) => {
            console.error('Unhandled promise rejection:', event.reason);
            this.handleError(event.reason);
            event.preventDefault();
        });

        window.addEventListener('error', (event) => {
            console.error('JavaScript error:', event.error);
            this.handleError(event.error);
        });

        window.addEventListener('offline', () => {
            this.showMessage('네트워크 연결이 끊어졌습니다.', 'warning');
        });

        window.addEventListener('online', () => {
            this.showMessage('네트워크 연결이 복구되었습니다.', 'success');
        });
    }

    handleError(error) {
        console.error('Error:', error);

        let message = '예상치 못한 오류가 발생했습니다.';
        let type = 'error';

        if (error) {
            if (error instanceof ApiError || error.status) {
                switch (error.status) {
                    case 400:
                        message = error.message || '잘못된 요청입니다.';
                        break;
                    case 401:
                        message = '로그인이 만료되었습니다. 다시 로그인해주세요.';
                        this.handleUnauthorized();
                        return;
                    case 403:
                        message = '접근 권한이 없습니다.';
                        break;
                    case 404:
                        message = '요청한 리소스를 찾을 수 없습니다.';
                        break;
                    case 500:
                        message = '서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.';
                        break;
                    default:
                        message = error.message || message;
                }
            } else if (error.message) {
                message = error.message;
            }
        }

        if (this.state) {
            this.state.setError(message);
        }

        this.showMessage(message, type);
    }

    handleUnauthorized() {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');

        if (this.state) {
            this.state.setUser(null);
        }

        if (!window.location.pathname.includes('login.html')) {
            this.showMessage('로그인이 필요합니다.', 'warning');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        }
    }

    showMessage(message, type = 'error') {
        const errorDiv = document.getElementById('error-message');
        if (errorDiv) {
            errorDiv.textContent = message;
            errorDiv.style.display = 'block';
            errorDiv.className = `error-message ${type}`;
            setTimeout(() => {
                errorDiv.style.display = 'none';
            }, type === 'error' ? 10000 : 5000);
        }

        this.showGlobalMessage(message, type);
    }

    showGlobalMessage(message, type = 'error') {
        this.removeGlobalMessage();

        const messageDiv = document.createElement('div');
        messageDiv.id = 'global-message';
        messageDiv.className = `global-message global-message-${type}`;
        messageDiv.innerHTML = `
            <span>${message}</span>
            <button onclick="GlobalErrorHandler.getInstance().removeGlobalMessage()">&times;</button>
        `;

        document.body.insertBefore(messageDiv, document.body.firstChild);

        const timeout = type === 'error' ? 10000 : 5000;
        setTimeout(() => {
            this.removeGlobalMessage();
        }, timeout);
    }

    removeGlobalMessage() {
        const existingMessage = document.getElementById('global-message');
        if (existingMessage) {
            existingMessage.remove();
        }
    }

    clearError() {
        if (this.state) {
            this.state.setError(null);
        }
        
        const errorDiv = document.getElementById('error-message');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
        
        this.removeGlobalMessage();
    }

    reportError(error, context = '') {
        console.error(`Error in ${context}:`, error);
        this.handleError(error);
    }

    showErrorMessage(message) {
        this.showMessage(message, 'error');
    }
}

window.reportError = (error, context) => {
    GlobalErrorHandler.getInstance().reportError(error, context);
};

window.clearError = () => {
    GlobalErrorHandler.getInstance().clearError();
};
