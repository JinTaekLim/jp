// 사용자 정보 전역 관리 유틸리티
class UserManager {
    constructor() {
        this.storageKey = 'jp_currentUser';
        this.currentUser = null;
        this.loadFromStorage();
    }

    // sessionStorage에서 사용자 정보 로드
    loadFromStorage() {
        try {
            const stored = sessionStorage.getItem(this.storageKey);
            if (stored) {
                this.currentUser = JSON.parse(stored);
            }
        } catch (error) {
            console.error('사용자 정보 로드 실패:', error);
            this.clearUser();
        }
    }

    // sessionStorage에 사용자 정보 저장
    saveToStorage() {
        try {
            if (this.currentUser) {
                sessionStorage.setItem(this.storageKey, JSON.stringify(this.currentUser));
            } else {
                sessionStorage.removeItem(this.storageKey);
            }
        } catch (error) {
            console.error('사용자 정보 저장 실패:', error);
        }
    }

    // API에서 사용자 정보 로드
    async loadUserInfo() {
        try {
            const response = await apiGet('/api/users/me');
            if (response) {
                const result = await response.json();
                if (result.success) {
                    this.setUser(result.data);
                    return true;
                }
            }
        } catch (error) {
            console.error('사용자 정보 로드 실패:', error);
        }

        // 실패 시 GUEST로 처리
        this.setUser({ name: 'GUEST', email: 'GUEST', role: 'GUEST' });
        return false;
    }

    // 사용자 정보 설정
    setUser(user) {
        this.currentUser = user;
        this.saveToStorage();
    }

    // 현재 사용자 정보 반환
    getCurrentUser() {
        return this.currentUser;
    }

    // GUEST 사용자인지 확인
    isGuest() {
        return !this.currentUser || this.currentUser.role === 'GUEST';
    }

    // 인증된 사용자인지 확인
    isAuthenticated() {
        return this.currentUser && this.currentUser.role !== 'GUEST';
    }

    // 사용자 정보 초기화 (로그아웃 시)
    clearUser() {
        this.currentUser = null;
        sessionStorage.removeItem(this.storageKey);
    }

    // 사용자 인터페이스 업데이트 (공통 함수)
    updateUserInterface(userName, authBtn, authIcon, authText) {
        const user = this.getCurrentUser();

        if (!user) return;

        // 사용자 이름 표시
        if (userName) {
            userName.textContent = user.name;
        }

        // 로그인/로그아웃 버튼 설정
        if (authBtn && authIcon && authText) {
            authBtn.style.display = 'flex';

            if (user.role === 'GUEST') {
                // 로그인 버튼
                authBtn.className = 'auth-btn';
                authIcon.setAttribute('d', 'M15 3H17C18.1046 3 19 3.89543 19 5V19C19 20.1046 18.1046 21 17 21H15M10 17L15 12L10 7M15 12H3');
                authText.textContent = '로그인';
            } else {
                // 로그아웃 버튼
                authBtn.className = 'auth-btn logout-style';
                authIcon.setAttribute('d', 'M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9');
                authText.textContent = '로그아웃';
            }
        }
    }

    // 인증 버튼 클릭 처리 (공통 함수)
    handleAuth() {
        if (this.isGuest()) {
            // 로그인 페이지로 이동
            window.location.href = '/page/login';
        } else {
            // 로그아웃 처리
            this.logout();
        }
    }

    // 로그아웃 처리
    async logout() {
        if (confirm('로그아웃 하시겠습니까?')) {
            try {
                await apiPost('/api/users/logout', {});
                this.clearUser(); // 사용자 정보 초기화
                window.location.href = '/page/study/mode'; // 모드 선택 페이지로 이동
            } catch (error) {
                console.error('로그아웃 오류:', error);
                this.clearUser(); // 오류가 발생해도 정보 초기화
                window.location.href = '/page/study/mode'; // 모드 선택 페이지로 이동
            }
        }
    }
}

// 전역 UserManager 인스턴스 생성
window.userManager = new UserManager();

// 전역 함수들
function handleAuth() {
    window.userManager.handleAuth();
}

function logout() {
    window.userManager.logout();
}

function goToLogin() {
    window.location.href = '/page/login';
}