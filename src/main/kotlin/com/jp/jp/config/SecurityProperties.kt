package com.jp.jp.config

import org.springframework.stereotype.Component

/**
 * Security 관련 설정값들을 관리하는 클래스
 */
@Component
class SecurityProperties {

    /**
     * 인증 없이 접근 가능한 모든 경로를 반환함
     */
    fun getPublicEndpoints(): Array<String> {
        return arrayOf(
            "/api/users", // POST 회원가입
            "/api/users/login",
            "/api/users/me", // 현재 사용자 정보 조회 (GUEST 포함)
            "/api/jlpt-words/random", // 랜덤 JLPT 단어 조회 (GUEST 접근 가능)
            "/api/jlpt-words/random/**", // 랜덤 JLPT 단어 관련 모든 하위 경로
            "/api/collect/**", // collect API는 일단 인증 없이 접근 가능
            "/page/**", // 프론트엔드 페이지들
            "/css/**", // CSS 정적 리소스
            "/js/**", // JavaScript 정적 리소스
            "/images/**", // 이미지 정적 리소스
            "/favicon.ico", // 파비콘
            "/swagger-ui/**", // Swagger UI 정적 리소스
            "/swagger-ui.html", // Swagger UI 메인 페이지
            "/v3/api-docs/**", // OpenAPI 3 문서
            "/manage/**", // Spring Boot Actuator 관리 경로
            "/actuator/**", // Spring Boot Actuator
            "/h2-console/**" // H2 데이터베이스 콘솔
        )
    }
}