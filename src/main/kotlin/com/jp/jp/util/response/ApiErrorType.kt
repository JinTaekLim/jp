package com.jp.jp.util.response

/**
 * API 에러 타입 열거형
 */
enum class ApiErrorType(val message: String) {
    // 일반 에러
    SUCCESS("성공"),
    BAD_REQUEST("잘못된 요청입니다"),
    VALIDATION_ERROR("입력값 검증에 실패했습니다"),
    UNAUTHORIZED("인증이 필요합니다"),
    FORBIDDEN("접근 권한이 없습니다"),
    NOT_FOUND("요청한 리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다"),

    // 비즈니스 에러
    USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다"),
    INVALID_PASSWORD("비밀번호가 올바르지 않습니다"),
    TOKEN_EXPIRED("토큰이 만료되었습니다"),
    INVALID_TOKEN("유효하지 않은 토큰입니다")
}