package com.jp.jp.util.auth

import com.jp.jp.domain.users.entity.UserRole
import com.jp.jp.util.jwt.TokenManager
import org.springframework.stereotype.Component

/**
 * 인증 토큰 발급 및 쿠키 설정을 담당하는 클래스
 */
@Component
class AuthProvider(
    private val tokenManager: TokenManager,
    private val httpUtil: HttpUtil,
    private val authManager: AuthManager
) {

    /**
     * 사용자 로그인 시 액세스 토큰과 리프레시 토큰을 발급하고 쿠키에 설정
     */
    fun issueTokensAndSetCookies(userId: Long, userType: UserRole) {
        // 액세스 토큰과 리프레시 토큰 생성
        val accessToken = tokenManager.getAccessToken(userId, userType)
        val refreshToken = tokenManager.getRefreshToken(userId, userType)

        // HttpUtil을 통해 쿠키에 토큰 설정
        val response = httpUtil.getResponse()
        httpUtil.setAccessTokenCookie(response, accessToken)
        httpUtil.setRefreshTokenCookie(response, refreshToken)
    }

    /**
     * 로그아웃 처리 - 쿠키 삭제 및 보안 컨텍스트 정리
     */
    fun logout() {
        authManager.clearAuthentication()
    }
}