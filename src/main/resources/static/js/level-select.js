// 레벨 선택 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 사용자 정보 로드 (API 호출은 여기서만)
    loadUserInfo();

    // 저장된 단어수 설정값 불러오기
    loadWordCountSetting();

    // URL에서 study mode 파라미터 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const studyMode = urlParams.get('mode') || sessionStorage.getItem('jp_studyMode') || 'individual';

    // study mode에 따라 UI 업데이트
    updateUIForStudyMode(studyMode);

    // 레벨 버튼들에 애니메이션 추가
    const levelButtons = document.querySelectorAll('.level-btn');

    levelButtons.forEach((button, index) => {
        // 순차적으로 나타나는 애니메이션
        button.style.opacity = '0';
        button.style.transform = 'translateY(20px)';

        setTimeout(() => {
            button.style.transition = 'all 0.5s ease';
            button.style.opacity = '1';
            button.style.transform = 'translateY(0)';
        }, index * 100);
    });

    // 단어수 입력 필드 변경 이벤트 추가
    const wordCountInput = document.getElementById('wordCount');
    if (wordCountInput) {
        wordCountInput.addEventListener('input', saveWordCountSetting);
        wordCountInput.addEventListener('change', saveWordCountSetting);
    }
});

// localStorage에서 단어수 설정값 불러오기
function loadWordCountSetting() {
    const savedWordCount = localStorage.getItem('jp_wordCount');
    const wordCountInput = document.getElementById('wordCount');

    if (savedWordCount && wordCountInput) {
        wordCountInput.value = savedWordCount;
    }
}

// localStorage에 단어수 설정값 저장하기
function saveWordCountSetting() {
    const wordCountInput = document.getElementById('wordCount');
    if (wordCountInput) {
        localStorage.setItem('jp_wordCount', wordCountInput.value);
    }
}

// 레벨 선택하여 학습 시작
function startStudy(level) {
    // 단어 개수 입력값 가져오기
    const wordCountInput = document.getElementById('wordCount');
    const wordCount = wordCountInput ? wordCountInput.value : 20;

    // 현재 사용자 정보 확인
    const user = window.userManager.getCurrentUser();

    // study mode 확인
    const urlParams = new URLSearchParams(window.location.search);
    const studyMode = urlParams.get('mode') || sessionStorage.getItem('jp_studyMode') || 'individual';

    // 선택한 버튼에 로딩 효과 추가
    const button = event.target.closest('.level-btn');
    if (button) {
        button.style.opacity = '0.6';
        button.style.transform = 'scale(0.98)';

        // study mode와 사용자 타입에 따라 API 분기
        let studyUrl;

        if (studyMode === 'balanced') {
            // 균등 학습 모드: 균등 분배 API 사용
            studyUrl = `/page/study/words/${level}/balanced?count=${wordCount}`;
        } else {
            // 개별 학습 모드: 기존 로직
            if (user && user.role === 'GUEST') {
                // GUEST 사용자: random API 사용
                studyUrl = `/page/study/words/${level}/random?count=${wordCount}`;
            } else {
                // 로그인 사용자: personalized API 사용
                studyUrl = `/page/study/words/${level}?count=${wordCount}`;
            }
        }

        // 페이지 이동
        setTimeout(() => {
            window.location.href = studyUrl;
        }, 200);
    }
}

// 레벨별 설명 툴팁 (선택사항)
const levelDescriptions = {
    'N5': '기초 일본어 (약 800단어)',
    'N4': '초급 일본어 (약 1,500단어)',
    'N3': '중급 일본어 (약 3,000단어)',
    'N2': '중상급 일본어 (약 6,000단어)',
    'N1': '고급 일본어 (약 10,000단어)'
};

// 레벨 버튼 호버 시 설명 표시 (선택사항)
document.addEventListener('DOMContentLoaded', function() {
    const levelButtons = document.querySelectorAll('.level-btn');

    levelButtons.forEach(button => {
        button.addEventListener('mouseenter', function() {
            const level = this.querySelector('.level-text').textContent;
            const description = levelDescriptions[level];

            if (description) {
                // 툴팁 생성 및 표시
                showTooltip(this, description);
            }
        });

        button.addEventListener('mouseleave', function() {
            // 툴팁 제거
            hideTooltip();
        });
    });
});

let currentTooltip = null;

function showTooltip(element, text) {
    // 기존 툴팁 제거
    hideTooltip();

    const tooltip = document.createElement('div');
    tooltip.className = 'level-tooltip';
    tooltip.textContent = text;
    tooltip.style.cssText = `
        position: absolute;
        background: #333;
        color: white;
        padding: 8px 12px;
        border-radius: 6px;
        font-size: 12px;
        white-space: nowrap;
        z-index: 1000;
        pointer-events: none;
        opacity: 0;
        transition: opacity 0.2s ease;
    `;

    document.body.appendChild(tooltip);

    // 위치 계산
    const rect = element.getBoundingClientRect();
    tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
    tooltip.style.top = rect.bottom + 8 + 'px';

    // 애니메이션
    setTimeout(() => {
        tooltip.style.opacity = '1';
    }, 10);

    currentTooltip = tooltip;
}

function hideTooltip() {
    if (currentTooltip) {
        currentTooltip.style.opacity = '0';
        setTimeout(() => {
            if (currentTooltip && currentTooltip.parentNode) {
                currentTooltip.parentNode.removeChild(currentTooltip);
            }
            currentTooltip = null;
        }, 200);
    }
}

// 키보드 네비게이션 지원
document.addEventListener('keydown', function(event) {
    const levelButtons = document.querySelectorAll('.level-btn');
    const currentFocus = document.activeElement;
    const currentIndex = Array.from(levelButtons).indexOf(currentFocus);

    switch(event.key) {
        case 'ArrowDown':
            event.preventDefault();
            if (currentIndex < levelButtons.length - 1) {
                levelButtons[currentIndex + 1].focus();
            }
            break;

        case 'ArrowUp':
            event.preventDefault();
            if (currentIndex > 0) {
                levelButtons[currentIndex - 1].focus();
            }
            break;

        case 'Enter':
        case ' ':
            event.preventDefault();
            if (currentFocus && currentFocus.classList.contains('level-btn')) {
                const level = currentFocus.querySelector('.level-text').textContent;
                startStudy(level);
            }
            break;
    }
});

// 사용자 정보 로드 (이미 로드된 정보 사용)
async function loadUserInfo() {
    // 사용자 정보가 없는 경우에만 API 호출 (직접 접근한 경우)
    const user = window.userManager.getCurrentUser();
    if (!user) {
        await window.userManager.loadUserInfo();
    }

    updateUserInterface();
    console.log('Level-select: Current user:', window.userManager.getCurrentUser());
}

// 사용자 인터페이스 업데이트
function updateUserInterface() {
    const userName = document.getElementById('userName');
    const authBtn = document.getElementById('authBtn');
    const authIcon = document.getElementById('authIcon');
    const authText = document.getElementById('authText');
    const wordCountInput = document.getElementById('wordCount');
    const wordCountSection = document.querySelector('.word-count-section');
    const levelGuestInfo = document.getElementById('levelGuestInfo');

    // 기본 사용자 인터페이스 업데이트 (user-manager 사용)
    window.userManager.updateUserInterface(userName, authBtn, authIcon, authText);

    const user = window.userManager.getCurrentUser();

    // GUEST 사용자 처리
    if (user && user.role === 'GUEST') {
        // GUEST 안내 메시지 표시
        if (levelGuestInfo) {
            levelGuestInfo.style.display = 'block';
        }

        // 단어 개수 고정 및 비활성화
        if (wordCountInput) {
            wordCountInput.value = 20;
            wordCountInput.disabled = true;
            wordCountInput.style.backgroundColor = '#F0F0F0';
            wordCountInput.style.color = '#666';
            wordCountInput.style.cursor = 'not-allowed';
        }

        // 단어 개수 섹션 스타일 변경
        if (wordCountSection) {
            wordCountSection.classList.add('guest-disabled');
        }
    } else {
        // 로그인 사용자 처리
        if (levelGuestInfo) {
            levelGuestInfo.style.display = 'none';
        }

        if (wordCountInput) {
            wordCountInput.disabled = false;
            wordCountInput.style.backgroundColor = '#FFFFFF';
            wordCountInput.style.color = '#007AFF';
            wordCountInput.style.cursor = 'text';
        }

        if (wordCountSection) {
            wordCountSection.classList.remove('guest-disabled');
        }
    }
}


// 모드 선택 페이지로 이동
function goToModeSelect() {
    window.location.href = '/page/study/mode';
}

// study mode에 따라 UI 업데이트
function updateUIForStudyMode(studyMode) {
    const levelTitle = document.querySelector('.level-title');
    const levelGuestInfo = document.getElementById('levelGuestInfo');

    if (studyMode === 'balanced') {
        // 균등 학습 모드
        if (levelTitle) {
            levelTitle.textContent = '균등 학습 - 레벨 선택';
        }

        // GUEST 사용자 안내 메시지 수정
        if (levelGuestInfo) {
            const infoText = levelGuestInfo.querySelector('.level-guest-info-text');
            if (infoText) {
                infoText.innerHTML = `
                    <span class="level-guest-info-highlight">균등 학습 모드</span>에서는 목표 레벨까지의<br>
                    모든 단어를 균등하게 분배하여 학습합니다!
                `;
            }
        }
    } else {
        // 개별 학습 모드 (기본)
        if (levelTitle) {
            levelTitle.textContent = '개별 학습 - 레벨 선택';
        }

        // 기본 GUEST 사용자 안내 메시지 유지
        if (levelGuestInfo) {
            const infoText = levelGuestInfo.querySelector('.level-guest-info-text');
            if (infoText) {
                infoText.innerHTML = `
                    <span class="level-guest-info-highlight">로그인</span> 후에는 사용자 맞춤 학습을 제공합니다!<br>
                    학습 진도와 복습 일정을 관리하실 수 있어요.
                `;
            }
        }
    }
}