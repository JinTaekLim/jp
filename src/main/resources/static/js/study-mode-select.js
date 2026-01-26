// 학습 모드 선택 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 사용자 정보 로드
    loadUserInfo();

    // 모드 버튼들에 애니메이션 추가
    const modeButtons = document.querySelectorAll('.mode-btn');

    modeButtons.forEach((button, index) => {
        // 순차적으로 나타나는 애니메이션
        button.style.opacity = '0';
        button.style.transform = 'translateY(30px)';

        setTimeout(() => {
            button.style.transition = 'all 0.6s ease';
            button.style.opacity = '1';
            button.style.transform = 'translateY(0)';
        }, index * 200);
    });
});

// 학습 모드 선택
function selectStudyMode(mode) {
    // 선택한 버튼에 로딩 효과 추가
    const button = event.target.closest('.mode-btn');
    if (button) {
        button.style.opacity = '0.7';
        button.style.transform = 'scale(0.98)';

        // 선택한 모드를 sessionStorage에 저장
        sessionStorage.setItem('jp_studyMode', mode);

        // 레벨 선택 페이지로 이동
        setTimeout(() => {
            window.location.href = `/page/study/level?mode=${mode}`;
        }, 300);
    }
}

// 사용자 정보 로드 (항상 최신 정보 확인)
async function loadUserInfo() {
    // 모드 선택 페이지에서는 항상 API를 호출하여 최신 사용자 정보 확인
    await window.userManager.loadUserInfo();

    updateUserInterface();
    console.log('Study-mode-select: Current user:', window.userManager.getCurrentUser());

    const currentUser = window.userManager.getCurrentUser();
    // GUEST인 경우 팝업 표시
    if (currentUser && currentUser.role === 'GUEST') {
        console.log('Study-mode-select: Showing guest popup for GUEST user');
        showGuestPopup();
    } else {
        console.log('Study-mode-select: Not showing popup:', {
            hasUser: !!currentUser,
            userRole: currentUser?.role,
            isGuest: currentUser?.role === 'GUEST'
        });
    }
}

// 사용자 인터페이스 업데이트
function updateUserInterface() {
    const userName = document.getElementById('userName');
    const authBtn = document.getElementById('authBtn');
    const authIcon = document.getElementById('authIcon');
    const authText = document.getElementById('authText');

    // 기본 사용자 인터페이스 업데이트 (user-manager 사용)
    window.userManager.updateUserInterface(userName, authBtn, authIcon, authText);
}

// GUEST 알림 툴팁 표시
function showGuestPopup() {
    const popupHtml = `
        <div class="guest-popup" id="guestPopup">
            <div class="guest-popup-content">
                <p>로그인하면 개인 맞춤형 복습과 학습 기록을 이용하실 수 있습니다.</p>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', popupHtml);

    const popup = document.getElementById('guestPopup');

    // 애니메이션을 위해 잠시 후 show 클래스 추가
    setTimeout(() => {
        popup.classList.add('show');
    }, 50);

    // 5초 후 자동으로 팝업 닫기
    const autoCloseTimer = setTimeout(() => {
        closeGuestPopup();
    }, 5000);

    // 팝업 클릭시 닫기 (사용자가 원할 때만)
    popup.addEventListener('click', function(event) {
        // 팝업 내용이 아닌 곳을 클릭했을 때만 닫기
        if (event.target === popup) {
            clearTimeout(autoCloseTimer);
            closeGuestPopup();
        }
    });

    // 팝업 저장 (타이머 정리를 위해)
    popup.autoCloseTimer = autoCloseTimer;
}

// GUEST 팝업 닫기
function closeGuestPopup() {
    const popup = document.getElementById('guestPopup');
    if (popup) {
        // 자동 닫기 타이머가 있다면 정리
        if (popup.autoCloseTimer) {
            clearTimeout(popup.autoCloseTimer);
        }

        // 닫기 애니메이션
        popup.classList.remove('show');

        // 애니메이션 완료 후 제거
        setTimeout(() => {
            popup.remove();
        }, 300);
    }
}

// 로그인 페이지로 이동
function goToLogin() {
    window.location.href = '/page/login';
}

// 키보드 네비게이션 지원
document.addEventListener('keydown', function(event) {
    const modeButtons = document.querySelectorAll('.mode-btn');
    const currentFocus = document.activeElement;
    const currentIndex = Array.from(modeButtons).indexOf(currentFocus);

    switch(event.key) {
        case 'ArrowDown':
        case 'ArrowRight':
            event.preventDefault();
            if (currentIndex < modeButtons.length - 1) {
                modeButtons[currentIndex + 1].focus();
            } else {
                modeButtons[0].focus();
            }
            break;

        case 'ArrowUp':
        case 'ArrowLeft':
            event.preventDefault();
            if (currentIndex > 0) {
                modeButtons[currentIndex - 1].focus();
            } else {
                modeButtons[modeButtons.length - 1].focus();
            }
            break;

        case 'Enter':
        case ' ':
            event.preventDefault();
            if (currentFocus && currentFocus.classList.contains('mode-btn')) {
                const mode = currentFocus.classList.contains('balanced-mode') ? 'balanced' : 'individual';
                selectStudyMode(mode);
            }
            break;
    }
});