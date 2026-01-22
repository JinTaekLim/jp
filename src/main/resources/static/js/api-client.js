// 공통 API 클라이언트 유틸리티
// 모든 API 호출에서 인증 오류를 일괄 처리

// 공통 API 클라이언트 함수
async function apiCall(url, options = {}) {
    const defaultOptions = {
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        },
        ...options
    };

    try {
        const response = await fetch(url, defaultOptions);

        // 인증 오류 처리 (401, 403)
        if (response.status === 401 || response.status === 403) {
            window.location.href = '/page/login';
            return null;
        }

        // 기타 HTTP 오류 처리
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return response;
    } catch (error) {
        console.error('API 호출 오류:', error);
        throw error;
    }
}

// GET 요청 헬퍼
async function apiGet(url) {
    return apiCall(url, { method: 'GET' });
}

// POST 요청 헬퍼
async function apiPost(url, data) {
    return apiCall(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

// PUT 요청 헬퍼
async function apiPut(url, data) {
    return apiCall(url, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

// DELETE 요청 헬퍼
async function apiDelete(url) {
    return apiCall(url, { method: 'DELETE' });
}