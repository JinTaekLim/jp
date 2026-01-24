// 인증 관련 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const signupForm = document.getElementById('signupForm');
    const errorMessage = document.getElementById('errorMessage');

    // 로그인 폼 처리
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // 회원가입 폼 처리
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }
});

// 로그인 처리
async function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await apiPost('/api/users/login', {
            email: email,
            password: password
        });

        // apiPost에서 null 반환시 인증 오류로 리다이렉트된 상태
        if (!response) return;

        const data = await response.json();

        // 로그인 성공 - 바로 레벨 선택 페이지로 이동
        window.location.href = '/page/study/level';
    } catch (error) {
        console.error('Login error:', error);
        showError('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
}

// 회원가입 처리
async function handleSignup(event) {
    event.preventDefault();

    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    // 비밀번호 확인
    if (password !== confirmPassword) {
        showError('비밀번호가 일치하지 않습니다.');
        return;
    }

    try {
        const response = await apiPost('/api/users', {
            email: email,
            name: name,
            password: password
        });

        // apiPost에서 null 반환시 인증 오류로 리다이렉트된 상태
        if (!response) return;

        const data = await response.json();

        // 회원가입 성공 - 바로 로그인 페이지로 이동
        window.location.href = '/page/login';
    } catch (error) {
        console.error('Signup error:', error);
        showError('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
}

// 에러 메시지 표시
function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        errorDiv.style.backgroundColor = '#FFEBEE';
        errorDiv.style.color = '#D32F2F';

        // 3초 후 자동 숨김
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 3000);
    }
}

// 성공 메시지 표시
function showMessage(message, type) {
    const errorDiv = document.getElementById('errorMessage');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';

        if (type === 'success') {
            errorDiv.style.backgroundColor = '#E8F5E8';
            errorDiv.style.color = '#2E7D32';
        }

        // 3초 후 자동 숨김
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 3000);
    }
}

// 입력 필드 실시간 검증
document.addEventListener('DOMContentLoaded', function() {
    const inputs = document.querySelectorAll('input');

    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateInput(this);
        });
    });
});

function validateInput(input) {
    const value = input.value.trim();

    switch(input.type) {
        case 'email':
            if (value && !isValidEmail(value)) {
                input.style.borderColor = '#D32F2F';
                showError('올바른 이메일 형식을 입력해주세요.');
            } else {
                input.style.borderColor = '#E5E5EA';
            }
            break;

        case 'password':
            if (value && value.length < 6) {
                input.style.borderColor = '#D32F2F';
                showError('비밀번호는 6자리 이상이어야 합니다.');
            } else {
                input.style.borderColor = '#E5E5EA';
            }
            break;

        default:
            if (value.length === 0) {
                input.style.borderColor = '#D32F2F';
            } else {
                input.style.borderColor = '#E5E5EA';
            }
    }
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}