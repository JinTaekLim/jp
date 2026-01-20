package com.jp.jp.util.jwt

import com.jp.jp.util.auth.HttpUtil
import com.jp.jp.util.auth.SecurityTransformer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JWT 기반 인증 필터 - 쿠키 방식
 * 액세스 토큰 우선 확인, 만료 시 리프레시 토큰으로 갱신하여 SecurityContext에 사용자 정보를 설정
 */
@Component
class JwtAuthenticationFilter(
    private val tokenManager: TokenManager,
    private val securityTransformer: SecurityTransformer,
    private val httpUtil: HttpUtil,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val validToken = getValidToken(request, response)

            validToken?.let {
                val userId = tokenManager.extractUserIdFromToken(it)
                val userType = tokenManager.extractUserTypeFromToken(it)

                securityTransformer.setContextHolder(userId.toString(), userType.name)
            }

            filterChain.doFilter(request, response)
        } finally {
            // 요청 처리 완료 후 SecurityContextHolder 정리
            securityTransformer.clearContextHolder()
        }
    }

    // 액세스 토큰 우선, 리프레시 토큰을 통한 갱신 로직
    private fun getValidToken(request: HttpServletRequest, response: HttpServletResponse): String? {
        // 1. 먼저 액세스 토큰을 시도
        val accessToken = httpUtil.getAccessTokenFromCookie(request)
        accessToken?.let { token ->
            runCatching {
                tokenManager.validateToken(token)
                token
            }.getOrNull()?.let { return it }
        }

        // 2. 액세스 토큰이 없거나 만료된 경우 리프레시 토큰으로 갱신
        return runCatching {
            val refreshToken = httpUtil.getRefreshTokenFromCookie(request)
            tokenManager.validateToken(refreshToken)

            // 리프레시 토큰에서 사용자 정보 추출하여 새로운 액세스 토큰 생성
            val userId = tokenManager.extractUserIdFromToken(refreshToken)
            val userType = tokenManager.extractUserTypeFromToken(refreshToken)

            tokenManager.getAccessToken(userId, userType).also { newAccessToken ->
                // 새로운 액세스 토큰을 쿠키에 설정
                httpUtil.setAccessTokenCookie(response, newAccessToken)
            }
        }.getOrNull()
    }
}