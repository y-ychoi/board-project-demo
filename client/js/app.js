/**
 * 메인 애플리케이션 클래스
 */
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
        const user = this.authManager.getUser();
        if (user) {
            this.stateManager.setUser(user);
        }

        window.addEventListener('unhandledrejection', (event) => {
            this.errorHandler.handleError(event.reason);
        });
    }

    async login(userId, password) {
        try {
            this.stateManager.setLoading(true);
            const result = await this.authManager.login(userId, password);
            this.stateManager.setUser(result.user);
            return result;
        } catch (error) {
            this.errorHandler.handleError(error);
            throw error;
        } finally {
            this.stateManager.setLoading(false);
        }
    }

    async signup(userData) {
        try {
            this.stateManager.setLoading(true);
            return await this.authManager.signup(userData);
        } catch (error) {
            this.errorHandler.handleError(error);
            throw error;
        } finally {
            this.stateManager.setLoading(false);
        }
    }

    logout() {
        this.authManager.logout();
        this.stateManager.setUser(null);
        window.location.href = 'login.html';
    }

    async loadBoards(page = 0, size = 10) {
        try {
            this.stateManager.setLoading(true);
            const boards = await this.boardService.getBoards(page, size);
            this.stateManager.setBoards(boards);
            return boards;
        } catch (error) {
            this.errorHandler.handleError(error);
            throw error;
        } finally {
            this.stateManager.setLoading(false);
        }
    }

    async loadBoard(boardNo) {
        try {
            this.stateManager.setLoading(true);
            const board = await this.boardService.getBoard(boardNo);
            this.stateManager.setCurrentBoard(board);
            return board;
        } catch (error) {
            this.errorHandler.handleError(error);
            throw error;
        } finally {
            this.stateManager.setLoading(false);
        }
    }

    async createBoard(boardData) {
        try {
            this.stateManager.setLoading(true);
            const result = await this.boardService.createBoard(boardData);
            
            // 게시글 작성 완료 신호를 localStorage에 저장
            localStorage.setItem('boardCreated', Date.now().toString());
            
            return result;
        } catch (error) {
            this.errorHandler.handleError(error);
            throw error;
        } finally {
            this.stateManager.setLoading(false);
        }
    }

    async updateBoard(boardNo, boardData) {
        try {
            this.stateManager.setLoading(true);
            return await this.boardService.updateBoard(boardNo, boardData);
        } catch (error) {
            this.errorHandler.handleError(error);
            throw error;
        } finally {
            this.stateManager.setLoading(false);
        }
    }

    async deleteBoard(boardNo) {
        try {
            this.stateManager.setLoading(true);
            await this.boardService.deleteBoard(boardNo);
        } catch (error) {
            this.errorHandler.handleError(error);
            throw error;
        } finally {
            this.stateManager.setLoading(false);
        }
    }

    isAuthenticated() {
        return this.authManager.isAuthenticated();
    }

    isAdmin() {
        return this.authManager.isAdmin();
    }

    getUser() {
        return this.authManager.getUser();
    }
}
