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
        this.isLoading = false; // API 호출 중 로딩 상태
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
            wordLevel: document.getElementById('wordLevel'),
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

        // 키보드 단축키 - 하나의 리스너만 등록
        this.keyHandler = (event) => this.handleKeyPress(event);
        document.addEventListener('keydown', this.keyHandler);

        // 페이지에 포커스를 주어 키보드 이벤트를 받을 수 있게 함
        document.body.tabIndex = -1;
        document.body.focus();

        console.log('Events bound successfully');
    }

    // 학습 시작
    async startStudy() {
        // 초기 로딩은 기본 로딩 요소만 사용 (인터랙션 차단 없이)
        if (this.elements.loading) {
            this.elements.loading.style.display = 'flex';
        }

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
            if (this.elements.loading) {
                this.elements.loading.style.display = 'none';
            }
        }
    }

    // API에서 단어 불러오기
    async loadWords() {
        // 사용자 타입과 학습 모드에 따라 다른 API 호출
        const user = window.userManager.getCurrentUser();
        const isGuest = user && user.role === 'GUEST';
        const studyMode = sessionStorage.getItem('jp_studyMode') || 'individual';

        let apiEndpoint;

        if (isGuest) {
            // 게스트 사용자: 항상 랜덤 API
            apiEndpoint = `/api/jlpt-words/random?level=${this.currentLevel}&count=${this.wordCount}`;
        } else if (studyMode === 'balanced') {
            // 균등 학습 모드: balanced API 사용
            apiEndpoint = `/api/jlpt-words/balanced?targetLevel=${this.currentLevel}&count=${this.wordCount}`;
        } else {
            // 개별 학습 모드: personalized API 사용
            apiEndpoint = `/api/jlpt-words/personalized?level=${this.currentLevel}&count=${this.wordCount}`;
        }

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

        // 품사와 레벨을 별도 박스에 표시
        this.elements.partOfSpeech.textContent = this.currentWord.partOfSpeech;
        this.elements.wordLevel.textContent = 'N' + this.currentWord.level;

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

        // 버튼 텍스트 초기화 (키보드 단축키 포함)
        this.elements.hiraganaBtn.textContent = '히라가나 보기 (Q)';
        this.elements.meaningBtn.textContent = '뜻 보기 (E)';
    }

    // 히라가나 보기
    showHiragana() {
        if (this.elements.hiraganaDisplay.style.display === 'none') {
            this.elements.hiraganaDisplay.textContent = this.currentWord.japanese;
            this.elements.hiraganaDisplay.style.display = 'block';
            this.elements.hiraganaBtn.textContent = '히라가나 숨기기 (Q)';
        } else {
            this.elements.hiraganaDisplay.style.display = 'none';
            this.elements.hiraganaBtn.textContent = '히라가나 보기 (Q)';
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

            this.elements.meaningDisplay.style.display = 'flex';
            this.elements.meaningBtn.textContent = '뜻 숨기기 (E)';
        } else {
            this.elements.meaningDisplay.style.display = 'none';
            this.elements.meaningBtn.textContent = '뜻 보기 (E)';
        }
    }

    // 다시 학습 표시
    async markAsRetry() {
        // 로딩 중이면 중복 실행 방지
        if (this.isLoading) return;

        // 다시 학습 목록에 추가
        this.retryWords.push(this.currentWord);

        // API 호출 - 실패 기록
        this.setButtonsDisabled(true);
        try {
            await this.recordStudyResult(this.currentWord.id, false);
        } catch (error) {
            console.error('Failed to record retry:', error);
        } finally {
            this.setButtonsDisabled(false);
        }

        this.studySession.wordsStudied++;
        this.moveToNextWord();
    }

    // 알고 있음 표시
    async markAsKnown() {
        // 로딩 중이면 중복 실행 방지
        if (this.isLoading) return;

        // API 호출 - 성공 기록
        this.setButtonsDisabled(true);
        try {
            await this.recordStudyResult(this.currentWord.id, true);
        } catch (error) {
            console.error('Failed to record success:', error);
        } finally {
            this.setButtonsDisabled(false);
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
        // 로딩 중이면 키보드 이벤트 차단
        if (this.isLoading) {
            return;
        }

        // 키 반복 이벤트 무시 (키를 꾹 누르고 있을 때 발생하는 이벤트)
        if (event.repeat) {
            return;
        }

        // 디버깅용 로그
        console.log('Key pressed:', event.key, 'Code:', event.code, 'Target:', event.target.tagName);

        // input, textarea, contenteditable 요소에서는 키보드 단축키 비활성화
        if (event.target.tagName === 'INPUT' ||
            event.target.tagName === 'TEXTAREA' ||
            event.target.contentEditable === 'true') {
            return;
        }

        const keyCode = event.code;
        const key = event.key.toLowerCase();
        console.log('Processing keyCode:', keyCode, 'key:', key);

        // 이벤트 중복 처리 방지 플래그
        let handled = false;

        // 물리적 키 위치로 우선 판단 (한글/영문 관계없음)
        switch(keyCode) {
            case 'KeyQ': // Q키 위치 (한글: ㅂ, 영문: q)
                event.preventDefault();
                console.log('Q key - showing hiragana');
                if (!this.elements.hiraganaBtn.disabled) {
                    this.showHiragana();
                }
                handled = true;
                break;
            case 'KeyE': // E키 위치 (한글: ㄷ, 영문: e)
                event.preventDefault();
                console.log('E key - showing meaning');
                this.showMeaning();
                handled = true;
                break;
            case 'KeyA': // A키 위치 (한글: ㅁ, 영문: a)
                event.preventDefault();
                console.log('A key - mark as retry');
                this.markAsRetry();
                handled = true;
                break;
            case 'KeyD': // D키 위치 (한글: ㅇ, 영문: d)
                event.preventDefault();
                console.log('D key - mark as known');
                this.markAsKnown();
                handled = true;
                break;
            case 'Escape':
                event.preventDefault();
                if (confirm('학습을 중단하시겠습니까?')) {
                    this.goToLevelSelect();
                }
                handled = true;
                break;
        }

        // 이미 처리된 경우 중복 실행 방지
        if (handled) {
            return;
        }

        // 키 코드로 처리되지 않은 특수한 경우만 key로 처리 (폴백)
        switch(key) {
            case 'q':
            case 'ㅂ': // 한글 ㅂ
                event.preventDefault();
                console.log('Q key (fallback) - showing hiragana');
                if (!this.elements.hiraganaBtn.disabled) {
                    this.showHiragana();
                }
                break;
            case 'e':
            case 'ㄷ': // 한글 ㄷ
                event.preventDefault();
                console.log('E key (fallback) - showing meaning');
                this.showMeaning();
                break;
            case 'a':
            case 'ㅁ': // 한글 ㅁ
                event.preventDefault();
                console.log('A key (fallback) - mark as retry');
                this.markAsRetry();
                break;
            case 'd':
            case 'ㅇ': // 한글 ㅇ
                event.preventDefault();
                console.log('D key (fallback) - mark as known');
                this.markAsKnown();
                break;
        }
    }

    // API 호출 중 사용자 입력 차단 (로딩창 없이 버튼만 비활성화)
    setButtonsDisabled(disabled) {
        this.isLoading = disabled;
        // 로딩창은 표시하지 않고 버튼 상태만 변경

        // 모든 버튼 비활성화/활성화
        const buttons = [
            this.elements.hiraganaBtn,
            this.elements.meaningBtn,
            this.elements.retryBtn,
            this.elements.knowBtn
        ];

        buttons.forEach(button => {
            if (button) {
                button.disabled = disabled;
                if (disabled) {
                    button.style.opacity = '0.6';
                    button.style.pointerEvents = 'none';
                    button.style.cursor = 'not-allowed';
                } else {
                    button.style.opacity = '1';
                    button.style.pointerEvents = 'auto';
                    button.style.cursor = 'pointer';
                }
            }
        });
    }

    // 학습 재시작
    restartStudy() {
        location.reload();
    }

    // 레벨 선택으로 이동 (모드 정보 유지)
    goToLevelSelect() {
        const studyMode = sessionStorage.getItem('jp_studyMode') || 'individual';
        window.location.href = `/page/study/level?mode=${studyMode}`;
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

// 앱 초기화 - 더 확실한 방법
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM Content Loaded');
    window.wordStudyApp = new WordStudyApp();

    // tabIndex와 focus 설정만 유지
    setTimeout(() => {
        console.log('Setting up keyboard focus');

        // body에 tabIndex와 focus 설정 (키보드 이벤트를 받기 위해 필요)
        if (document.body) {
            document.body.tabIndex = -1;
            document.body.focus();
        }

        console.log('Keyboard focus setup complete');
    }, 100);
});

// 페이지 종료 시 타이머 정리
window.addEventListener('beforeunload', function() {
    if (window.wordStudyApp && window.wordStudyApp.timerInterval) {
        clearInterval(window.wordStudyApp.timerInterval);
    }
});