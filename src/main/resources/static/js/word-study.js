// 단어 학습 페이지 JavaScript

class WordStudyApp {
    constructor() {
        // URL 파라미터에서 레벨과 단어 개수 가져오기
        const urlParams = new URLSearchParams(window.location.search);
        this.currentLevel = window.currentLevel || 'N5';
        this.wordCount = parseInt(urlParams.get('count')) || 20;

        this.words = [];
        this.currentWordIndex = 0;
        this.currentWord = null;
        this.retryWords = []; // 다시 학습할 단어들
        this.studySession = {
            startTime: new Date(),
            wordsStudied: 0,
            correctAnswers: 0,
            retryCount: 0
        };

        this.initializeElements();
        this.startStudy();
    }

    // DOM 요소 초기화
    initializeElements() {
        this.elements = {
            progressFill: document.getElementById('progressFill'),
            timer: document.getElementById('timer'),
            hiraganaDisplay: document.getElementById('hiraganaDisplay'),
            mainWord: document.getElementById('mainWord'),
            partOfSpeech: document.getElementById('partOfSpeech'),
            meaningDisplay: document.getElementById('meaningDisplay'),
            meaningList: document.getElementById('meaningList'),
            hiraganaBtn: document.getElementById('hiraganaBtn'),
            meaningBtn: document.getElementById('meaningBtn'),
            retryBtn: document.getElementById('retryBtn'),
            knowBtn: document.getElementById('knowBtn'),
            completeModal: document.getElementById('completeModal'),
            completeMessage: document.getElementById('completeMessage'),
            loading: document.getElementById('loading')
        };

        this.bindEvents();
        this.startTimer();
    }

    // 이벤트 바인딩
    bindEvents() {
        this.elements.hiraganaBtn.addEventListener('click', () => this.showHiragana());
        this.elements.meaningBtn.addEventListener('click', () => this.showMeaning());
        this.elements.retryBtn.addEventListener('click', () => this.markAsRetry());
        this.elements.knowBtn.addEventListener('click', () => this.markAsKnown());

        // 키보드 단축키
        document.addEventListener('keydown', (event) => this.handleKeyPress(event));
    }

    // 학습 시작
    async startStudy() {
        this.showLoading(true);

        try {
            await this.loadWords();
            if (this.words.length === 0) {
                this.showCompleteModal('현재 레벨에 학습할 단어가 없습니다.');
                return;
            }

            this.shuffleWords();
            this.showCurrentWord();
        } catch (error) {
            console.error('Failed to load words:', error);
            alert('단어를 불러오는데 실패했습니다. 다시 시도해주세요.');
        } finally {
            this.showLoading(false);
        }
    }

    // API에서 단어 불러오기
    async loadWords() {
        // 사용자 타입에 따라 다른 API 호출
        const user = window.userManager.getCurrentUser();
        const isGuest = user && user.role === 'GUEST';

        const apiEndpoint = isGuest
            ? `/api/jlpt-words/random?level=${this.currentLevel}&count=${this.wordCount}`
            : `/api/jlpt-words/personalized?level=${this.currentLevel}&count=${this.wordCount}`;

        const response = await apiGet(apiEndpoint);

        // apiGet에서 null 반환시 인증 오류로 리다이렉트된 상태
        if (!response) return;

        const data = await response.json();
        this.words = data.data || [];
    }

    // 단어 순서 섞기
    shuffleWords() {
        for (let i = this.words.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [this.words[i], this.words[j]] = [this.words[j], this.words[i]];
        }
    }

    // 현재 단어 표시
    showCurrentWord() {
        if (this.currentWordIndex >= this.words.length) {
            this.handleStudyComplete();
            return;
        }

        this.currentWord = this.words[this.currentWordIndex];
        this.resetWordDisplay();

        // 단어 표시 (한자 우선, 없으면 일본어)
        const displayText = this.currentWord.kanji || this.currentWord.japanese;
        this.elements.mainWord.textContent = displayText;

        // 품사 표시
        this.elements.partOfSpeech.textContent = this.currentWord.partOfSpeech;

        // 히라가나 버튼 활성화/비활성화
        const hasKanji = this.currentWord.kanji && this.currentWord.kanji !== this.currentWord.japanese;
        this.elements.hiraganaBtn.disabled = !hasKanji;

        this.updateProgress();
    }

    // 단어 표시 초기화
    resetWordDisplay() {
        this.elements.hiraganaDisplay.style.display = 'none';
        this.elements.hiraganaDisplay.textContent = '';
        this.elements.meaningDisplay.style.display = 'none';
        this.elements.meaningList.innerHTML = '';

        // 버튼 텍스트 초기화
        this.elements.hiraganaBtn.textContent = '히라가나 보기';
        this.elements.meaningBtn.textContent = '뜻 보기';
    }

    // 히라가나 보기
    showHiragana() {
        if (this.elements.hiraganaDisplay.style.display === 'none') {
            this.elements.hiraganaDisplay.textContent = this.currentWord.japanese;
            this.elements.hiraganaDisplay.style.display = 'block';
            this.elements.hiraganaBtn.textContent = '히라가나 숨기기';
        } else {
            this.elements.hiraganaDisplay.style.display = 'none';
            this.elements.hiraganaBtn.textContent = '히라가나 보기';
        }
    }

    // 의미 보기
    showMeaning() {
        if (this.elements.meaningDisplay.style.display === 'none') {
            // 의미 목록 생성
            this.elements.meaningList.innerHTML = '';
            this.currentWord.meanings.forEach(meaning => {
                const li = document.createElement('li');
                li.textContent = meaning;
                this.elements.meaningList.appendChild(li);
            });

            this.elements.meaningDisplay.style.display = 'block';
            this.elements.meaningBtn.textContent = '뜻 숨기기';
        } else {
            this.elements.meaningDisplay.style.display = 'none';
            this.elements.meaningBtn.textContent = '뜻 보기';
        }
    }

    // 다시 학습 표시
    async markAsRetry() {
        // 다시 학습 목록에 추가
        this.retryWords.push(this.currentWord);

        // API 호출 - 실패 기록
        try {
            await this.recordStudyResult(this.currentWord.id, false);
        } catch (error) {
            console.error('Failed to record retry:', error);
        }

        this.studySession.wordsStudied++;
        this.moveToNextWord();
    }

    // 알고 있음 표시
    async markAsKnown() {
        // API 호출 - 성공 기록
        try {
            await this.recordStudyResult(this.currentWord.id, true);
        } catch (error) {
            console.error('Failed to record success:', error);
        }

        this.studySession.wordsStudied++;
        this.studySession.correctAnswers++;
        this.moveToNextWord();
    }

    // 학습 결과 API 전송 (GUEST 사용자는 호출하지 않음)
    async recordStudyResult(wordId, isSuccess) {
        // GUEST 사용자인 경우 API 호출하지 않음
        if (window.userManager && window.userManager.isGuest()) {
            console.log('GUEST 사용자: 학습 결과 기록 생략');
            return null;
        }

        const endpoint = isSuccess ? '/api/word-learning/success' : '/api/word-learning/failure';

        const response = await apiPost(endpoint, { wordId: wordId });

        // apiPost에서 null 반환시 인증 오류로 리다이렉트된 상태
        if (!response) return;

        return await response.json();
    }

    // 다음 단어로 이동
    moveToNextWord() {
        this.currentWordIndex++;
        setTimeout(() => {
            this.showCurrentWord();
        }, 300);
    }

    // 진행률 업데이트
    updateProgress() {
        const totalWords = this.words.length;
        const progress = (this.currentWordIndex / totalWords) * 100;
        this.elements.progressFill.style.width = `${progress}%`;
    }

    // 학습 완료 처리
    handleStudyComplete() {
        if (this.retryWords.length > 0) {
            // 다시 학습할 단어들이 있으면 재시작
            this.handleRetryRound();
        } else {
            // 모든 학습 완료
            this.showCompleteModal(this.getCompleteMessage());
        }
    }

    // 재학습 라운드 처리
    handleRetryRound() {
        this.studySession.retryCount++;

        // 복습 알림 표시
        const message = `복습할 단어가 ${this.retryWords.length}개 있습니다.`;
        alert(message);

        // 자동으로 다시 학습할 단어들로 재시작
        this.words = [...this.retryWords];
        this.retryWords = [];
        this.currentWordIndex = 0;
        this.shuffleWords();
        this.showCurrentWord();
    }

    // 완료 메시지 생성
    getCompleteMessage() {
        const { correctAnswers } = this.studySession;
        const elapsedTime = this.getElapsedTime();

        return `
            학습 완료!

            ✅ 맞춘 단어: ${correctAnswers}개
            ⏱️ 소요 시간: ${elapsedTime}
        `;
    }

    // 완료 모달 표시
    showCompleteModal(message) {
        this.elements.completeMessage.innerHTML = message.replace(/\n/g, '<br>');
        this.elements.completeModal.style.display = 'flex';
    }

    // 타이머 시작
    startTimer() {
        this.timerInterval = setInterval(() => {
            const elapsed = this.getElapsedTime();
            this.elements.timer.textContent = elapsed;
        }, 1000);
    }

    // 경과 시간 계산
    getElapsedTime() {
        const now = new Date();
        const elapsed = Math.floor((now - this.studySession.startTime) / 1000);
        const minutes = Math.floor(elapsed / 60);
        const seconds = elapsed % 60;
        return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    }

    // 키보드 단축키 처리
    handleKeyPress(event) {
        switch(event.key) {
            case '1':
                event.preventDefault();
                if (!this.elements.hiraganaBtn.disabled) {
                    this.showHiragana();
                }
                break;
            case '2':
                event.preventDefault();
                this.showMeaning();
                break;
            case 'ArrowLeft':
                event.preventDefault();
                this.markAsRetry();
                break;
            case 'ArrowRight':
                event.preventDefault();
                this.markAsKnown();
                break;
            case 'Escape':
                event.preventDefault();
                if (confirm('학습을 중단하시겠습니까?')) {
                    this.goToLevelSelect();
                }
                break;
        }
    }

    // 로딩 표시
    showLoading(show) {
        this.elements.loading.style.display = show ? 'flex' : 'none';
    }

    // 학습 재시작
    restartStudy() {
        location.reload();
    }

    // 레벨 선택으로 이동
    goToLevelSelect() {
        window.location.href = '/page/study/level';
    }
}

// 전역 함수들 (HTML에서 호출)
function restartStudy() {
    if (window.wordStudyApp) {
        window.wordStudyApp.restartStudy();
    }
}

function goToLevelSelect() {
    if (window.wordStudyApp) {
        window.wordStudyApp.goToLevelSelect();
    }
}

// 앱 초기화
document.addEventListener('DOMContentLoaded', function() {
    window.wordStudyApp = new WordStudyApp();
});

// 페이지 종료 시 타이머 정리
window.addEventListener('beforeunload', function() {
    if (window.wordStudyApp && window.wordStudyApp.timerInterval) {
        clearInterval(window.wordStudyApp.timerInterval);
    }
});