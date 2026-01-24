// 학습 기록 페이지 무한스크롤 기능

class RecordManager {
    constructor() {
        this.recordList = document.getElementById('recordList');
        this.loadingMore = document.getElementById('loadingMore');
        this.noMoreData = document.getElementById('noMoreData');

        this.currentLastId = null;  // 현재 마지막 기록의 ID
        this.isLoading = false;     // 로딩 상태
        this.hasMore = true;        // 더 불러올 데이터가 있는지
        this.size = 5;              // 페이지 크기

        this.init();
    }

    init() {
        this.loadInitialRecords();
        this.setupInfiniteScroll();
    }

    // 초기 기록 로드
    async loadInitialRecords() {
        await this.loadRecords();
    }

    // 기록 데이터 로드
    async loadRecords() {
        if (this.isLoading || !this.hasMore) return;

        this.isLoading = true;
        this.showLoading();

        try {
            const url = this.buildApiUrl();
            const response = await apiGet(url);

            // apiGet에서 null 반환시 인증 오류로 리다이렉트된 상태
            if (!response) return;

            const result = await response.json();

            if (result.success) {
                this.handleLoadedRecords(result.data);
            } else {
                console.error('API 응답 오류:', result.message);
                this.showError('기록을 불러오는데 실패했습니다.');
            }

        } catch (error) {
            console.error('기록 로드 오류:', error);
            this.showError('네트워크 오류가 발생했습니다.');
        } finally {
            this.isLoading = false;
            this.hideLoading();
        }
    }

    // API URL 생성
    buildApiUrl() {
        let url = `/api/word-learning/studied-words?size=${this.size}`;
        if (this.currentLastId) {
            url += `&lastId=${this.currentLastId}`;
        }
        return url;
    }

    // 로드된 기록 처리
    handleLoadedRecords(data) {
        const { content, hasNext, nextLastId } = data;

        if (content && content.length > 0) {
            this.renderRecords(content);
            this.hasMore = hasNext;
            this.currentLastId = nextLastId;
        } else {
            this.hasMore = false;
        }

        if (!this.hasMore) {
            this.showNoMoreData();
        }
    }

    // 기록 렌더링
    renderRecords(records) {
        const sentinel = document.getElementById('scrollSentinel');
        const recordElements = records.map(record => this.createRecordElement(record));
        // sentinel 앞에 삽입하여 sentinel이 항상 맨 아래에 위치하도록 함
        recordElements.forEach(element => this.recordList.insertBefore(element, sentinel));
    }

    // 개별 기록 요소 생성
    createRecordElement(record) {
        const recordDiv = document.createElement('div');
        recordDiv.className = 'word-record';

        // 의미 목록을 문자열로 변환
        const meaningsText = record.meanings.join(', ');

        // 남은 복습 시간 포맷팅
        const remainingTimeText = this.formatRemainingTime(record.remainingDays, record.remainingHours, record.remainingMinutes);

        recordDiv.innerHTML = `
            <div class="record-header">
                <div class="record-word">${record.japanese}${record.kanji ? ` (${record.kanji})` : ''}</div>
                <div class="record-level">N${record.level}</div>
            </div>
            <div class="record-details">
                <div class="record-meanings">${meaningsText}</div>
                <div class="record-stats">
                    <div class="record-stat">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M9 11H5a2 2 0 0 0-2 2v3c0 .55.45 1 1 1h4V11z"/>
                            <path d="m15 11h4a2 2 0 0 1 2 2v3c0 .55-.45 1-1 1h-4V11z"/>
                            <path d="M12 2v9"/>
                        </svg>
                        ${record.totalAttempts}회 학습
                    </div>
                    <div class="record-stat">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <polyline points="12,6 12,12 16,14"/>
                        </svg>
                        ${remainingTimeText}
                    </div>
                </div>
            </div>
        `;

        return recordDiv;
    }

    // 남은 복습 시간을 사용자 친화적으로 포맷팅함 (우선순위: 일 > 시 > 분)
    formatRemainingTime(remainingDays, remainingHours, remainingMinutes) {
        // 우선순위 순서로 단일 단위만 표시
        if (remainingDays && remainingDays > 0) {
            return `${remainingDays}일 후 복습 예정`;
        }

        if (remainingHours && remainingHours > 0) {
            return `${remainingHours}시간 후 복습 예정`;
        }

        if (remainingMinutes && remainingMinutes > 0) {
            return `${remainingMinutes}분 후 복습 예정`;
        }

        // 모든 값이 null이거나 0이면 복습 예정 (복습 완료는 존재하지 않음)
        return '복습 예정';
    }

    // 무한스크롤 설정 (미리 로딩을 위해 rootMargin 증가)
    setupInfiniteScroll() {
        const observer = new IntersectionObserver(
            (entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting && this.hasMore && !this.isLoading) {
                        this.loadRecords();
                    }
                });
            },
            {
                root: null,
                rootMargin: '300px', // 화면 하단 300px 전에 미리 로딩 시작
                threshold: 0.1
            }
        );

        // 스크롤 감지를 위한 sentinel 요소 추가
        const sentinel = document.createElement('div');
        sentinel.id = 'scrollSentinel';
        sentinel.style.height = '1px';
        this.recordList.appendChild(sentinel);

        observer.observe(sentinel);
    }

    // 로딩 표시
    showLoading() {
        this.loadingMore.style.display = 'block';
        this.noMoreData.style.display = 'none';
    }

    // 로딩 숨김
    hideLoading() {
        this.loadingMore.style.display = 'none';
    }

    // 더 이상 데이터 없음 표시
    showNoMoreData() {
        this.noMoreData.style.display = 'block';
        this.loadingMore.style.display = 'none';
    }

    // 오류 메시지 표시
    showError(message) {
        // 간단한 에러 표시 (실제 프로젝트에서는 더 예쁜 에러 UI 사용)
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.textContent = message;
        errorDiv.style.margin = '20px';

        this.recordList.appendChild(errorDiv);

        // 3초 후 에러 메시지 제거
        setTimeout(() => {
            if (errorDiv.parentNode) {
                errorDiv.parentNode.removeChild(errorDiv);
            }
        }, 3000);
    }
}

// 사용자 정보 로드 (저장된 정보 사용, API 호출 없음)
function loadUserInfo() {
    const userName = document.getElementById('userName');
    const authBtn = document.getElementById('authBtn');
    const authIcon = document.getElementById('authIcon');
    const authText = document.getElementById('authText');

    // user-manager에서 사용자 정보 업데이트
    window.userManager.updateUserInterface(userName, authBtn, authIcon, authText);

    const user = window.userManager.getCurrentUser();

    // GUEST 사용자인 경우 안내 메시지 표시하고 기록 로딩 중단
    if (window.userManager.isGuest()) {
        showGuestMessage();
        return false; // 기록 로딩하지 않음
    }
    return true; // 기록 로딩 허용
}

// GUEST 안내 메시지 표시
function showGuestMessage() {
    const guestMessage = document.getElementById('guestMessage');
    const recordList = document.getElementById('recordList');
    const loadingMore = document.getElementById('loadingMore');
    const noMoreData = document.getElementById('noMoreData');

    // GUEST 메시지 표시
    if (guestMessage) {
        guestMessage.style.display = 'block';
    }

    // 기록 관련 요소들 숨기기
    if (recordList) {
        recordList.style.display = 'none';
    }
    if (loadingMore) {
        loadingMore.style.display = 'none';
    }
    if (noMoreData) {
        noMoreData.style.display = 'none';
    }
}

// 페이지 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    // 사용자 정보 로드 (저장된 정보 사용)
    const canLoadRecords = loadUserInfo();

    // 인증된 사용자(GUEST가 아닌 경우)만 기록 로드
    if (canLoadRecords) {
        new RecordManager();
    }
});