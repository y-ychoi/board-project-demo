/**
 * 좋아요 서비스 - REST API 호출 및 UI 관리
 */
class LikeService {
    constructor(apiClient) {
        this.apiClient = apiClient;
        this.isProcessing = false;  // 중복 클릭 방지
    }

    /**
     * 좋아요 토글 (낙관적 업데이트)
     */
    async toggleLike(boardNo) {
	    // 중복 클릭 방지
	    if (this.isProcessing) {
	        console.log('좋아요 처리 중입니다.');
	        return;
	    }
	
	    this.isProcessing = true;
	
	    // 현재 상태 저장 (롤백용)
	    const currentState = this.getCurrentLikeState();
	
	    try {
	        // 1. 낙관적 업데이트 - 즉시 UI 변경
	        this.updateUIOptimistically();
	
	        // 2. 서버 요청
	        const response = await this.apiClient.request(`/boards/${boardNo}/like`, {
	            method: 'POST'
	        });
	
	        // 🔍 디버깅: 응답 구조 확인
	        console.log('전체 응답:', response);
	        console.log('response.data:', response.data);
	
	        // 3. 서버 응답으로 동기화 (ApiResponseDto 구조 고려)
	        let responseData;
	        if (response.data && response.data.data) {
	            responseData = response.data.data;  // ApiResponseDto 구조
	        } else if (response.data) {
	            responseData = response.data;       // 직접 데이터
	        } else {
	            throw new Error('응답 데이터가 없습니다.');
	        }
	
	        console.log('실제 데이터:', responseData);
	        this.syncWithServer(responseData);
	
	        return responseData;
	
	    } catch (error) {
	        console.error('좋아요 API 오류:', error);
	        console.error('오류 상세:', error.response);
	
	        // 4. 실패시 롤백
	        this.rollbackToState(currentState);
	        throw error;
	    } finally {
	        this.isProcessing = false;
	    }
	}

    /**
     * 좋아요 상태 조회 (페이지 로딩시)
     */
    async getLikeStatus(boardNo) {
        try {
            const response = await this.apiClient.request(`/boards/${boardNo}/like`);
            return response.data;
        } catch (error) {
            console.error('좋아요 상태 조회 실패:', error);
            return { liked: false, likeCount: 0 };
        }
    }

    /**
     * 현재 UI 상태 저장 (롤백용)
     */
    getCurrentLikeState() {
        const likeBtn = document.getElementById('likeBtn');
        const likeIcon = document.getElementById('likeIcon');
        const likeCount = document.getElementById('likeCount');

        return {
            liked: likeBtn?.classList.contains('liked') || false,
            count: parseInt(likeCount?.textContent || '0'),
            icon: likeIcon?.textContent || '♡'
        };
    }

    /**
     * 낙관적 업데이트 - 즉시 UI 변경
     */
    updateUIOptimistically() {
        const likeBtn = document.getElementById('likeBtn');
        const likeIcon = document.getElementById('likeIcon');
        const likeCount = document.getElementById('likeCount');

        if (!likeBtn || !likeIcon || !likeCount) return;

        const isLiked = likeBtn.classList.contains('liked');
        const currentCount = parseInt(likeCount.textContent);

        if (isLiked) {
            // 좋아요 취소
            likeBtn.classList.remove('liked');
            likeIcon.textContent = '♡';
            likeCount.textContent = currentCount - 1;
        } else {
            // 좋아요 추가
            likeBtn.classList.add('liked');
            likeIcon.textContent = '♥';
            likeCount.textContent = currentCount + 1;
        }

        // 처리 중 표시
        likeBtn.classList.add('processing');
    }

    /**
     * 서버 응답으로 UI 동기화
     */
    syncWithServer(data) {
        const likeBtn = document.getElementById('likeBtn');
        const likeIcon = document.getElementById('likeIcon');
        const likeCount = document.getElementById('likeCount');

        if (!likeBtn || !likeIcon || !likeCount) return;

        // 서버 데이터로 정확한 상태 설정
        if (data.liked) {
            likeBtn.classList.add('liked');
            likeIcon.textContent = '♥';
        } else {
            likeBtn.classList.remove('liked');
            likeIcon.textContent = '♡';
        }

        likeCount.textContent = data.likeCount;

        // 처리 완료
        likeBtn.classList.remove('processing');
    }

    /**
     * 실패시 원래 상태로 롤백
     */
    rollbackToState(state) {
        const likeBtn = document.getElementById('likeBtn');
        const likeIcon = document.getElementById('likeIcon');
        const likeCount = document.getElementById('likeCount');

        if (!likeBtn || !likeIcon || !likeCount) return;

        if (state.liked) {
            likeBtn.classList.add('liked');
        } else {
            likeBtn.classList.remove('liked');
        }

        likeIcon.textContent = state.icon;
        likeCount.textContent = state.count;

        // 처리 완료
        likeBtn.classList.remove('processing');
    }

    /**
     * 초기 좋아요 상태 설정 (페이지 로딩시)
     */
    initializeLikeButton(isLiked, likeCount) {
        const likeBtn = document.getElementById('likeBtn');
        const likeIcon = document.getElementById('likeIcon');
        const likeCountEl = document.getElementById('likeCount');

        if (!likeBtn || !likeIcon || !likeCountEl) return;

        if (isLiked) {
            likeBtn.classList.add('liked');
            likeIcon.textContent = '♥';
        } else {
            likeBtn.classList.remove('liked');
            likeIcon.textContent = '♡';
        }

        likeCountEl.textContent = likeCount;
    }
}