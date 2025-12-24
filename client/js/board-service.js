/**
 * 게시판 서비스 클래스
 */
class BoardService {
    constructor(apiClient) {
        this.api = apiClient;
    }

    async getBoards(page = 0, size = 10) {
        return this.api.get('/boards', { page, size });
    }

    async getBoard(boardNo) {
        return this.api.get(`/boards/${boardNo}`);
    }

    async createBoard(boardData) {
        return this.api.post('/boards', boardData);
    }

    async updateBoard(boardNo, boardData) {
        return this.api.put(`/boards/${boardNo}`, boardData);
    }

    async deleteBoard(boardNo) {
        return this.api.delete(`/boards/${boardNo}`);
    }

    async getComments(boardNo) {
        return this.api.get(`/boards/${boardNo}/comments`);
    }

    async createComment(boardNo, content) {
        return this.api.post(`/boards/${boardNo}/comments`, { content });
    }

    async deleteComment(boardNo, commentNo) {
        return this.api.delete(`/boards/${boardNo}/comments/${commentNo}`);
    }
}
