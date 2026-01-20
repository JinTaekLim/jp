// 레벨 선택 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
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
});

// 레벨 선택하여 학습 시작
function startStudy(level) {
    // 단어 개수 입력값 가져오기
    const wordCountInput = document.getElementById('wordCount');
    const wordCount = wordCountInput ? wordCountInput.value : 20;

    // 선택한 버튼에 로딩 효과 추가
    const button = event.target.closest('.level-btn');
    if (button) {
        button.style.opacity = '0.6';
        button.style.transform = 'scale(0.98)';

        // 페이지 이동 - 단어 개수를 URL 파라미터로 전달
        setTimeout(() => {
            window.location.href = `/page/study/words/${level}?count=${wordCount}`;
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

// 로그아웃 기능
async function logout() {
    try {
        const response = await fetch('/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'same-origin' // 쿠키 포함
        });

        // 로그아웃 성공 (Spring Security는 302 리다이렉트 또는 200 응답)
        if (response.ok || response.redirected) {
            // 메인 페이지로 이동
            window.location.href = '/page/';
        } else {
            // 응답이 실패해도 클라이언트에서 메인으로 이동 (쿠키 삭제됨)
            window.location.href = '/page/';
        }
    } catch (error) {
        console.error('로그아웃 중 오류 발생:', error);
        // 네트워크 오류가 발생해도 메인으로 이동
        window.location.href = '/page/';
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