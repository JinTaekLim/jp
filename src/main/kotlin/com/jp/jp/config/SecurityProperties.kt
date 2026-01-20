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
            "/api/collect/**", // collect API는 일단 인증 없이 접근 가능
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/actuator/**",
            "/h2-console/**"
        )
    }
}