/**
 * 게시판 서비스 클래스
 */
class BoardService {
    constructor(apiClient) {
        this.api = apiClient;
    }

    async getBoards(page = 0, size = 10) {
        console.log(`API 호출: /boards?page=${page}&size=${size}`);
        const result = await this.api.get('/boards', { page, size });
        console.log('API 응답:', result);
        // REST API 응답에서 data 필드 추출
        return result.data || result;
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

	async updateComment(boardNo, commentNo, content) {
    	return this.api.put(`/boards/${boardNo}/comments/${commentNo}`, { content });
	}
    async deleteComment(boardNo, commentNo) {
        return this.api.delete(`/boards/${boardNo}/comments/${commentNo}`);
    }
}
