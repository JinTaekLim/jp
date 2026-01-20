package com.jp.jp.util.auth

import com.jp.jp.domain.users.entity.UserRole
import org.springframework.stereotype.Component

@Component
class AuthManager(
    private val httpUtil: HttpUtil,
    private val securityTransformer: SecurityTransformer,
    private val securityExtractor: SecurityExtractor,
    private val securityValidator: SecurityValidator,
) {
    fun setAuthentication(refreshToken: String) {
        // 쿠키에 refreshToken만 저장
        val response = httpUtil.getResponse()
        httpUtil.setRefreshTokenCookie(response, refreshToken)
    }

    fun getCurrentUserId(): Long {
        val authentication = securityExtractor.getAuthentication()
            ?: throw IllegalArgumentException("인증 정보가 없습니다.")
        securityValidator.validateAndGetUserId(authentication)
        return authentication.name.toLong()
    }

    /**
     * 현재 로그인한 사용자의 UserType을 반환
     * @return UserType (ADMIN, USER 등)
     */
    fun getCurrentUserType(): UserRole {
        val authentication = securityExtractor.getAuthentication()
            ?: throw IllegalArgumentException("인증 정보가 없습니다.")
        securityValidator.validateAndGetUserId(authentication)

        val authority = authentication.authorities.firstOrNull()
            ?: throw IllegalArgumentException("권한 정보가 없습니다.")

        val roleString = authority.authority.removePrefix("ROLE_")
        return UserRole.valueOf(roleString)
    }

    /**
     * 현재 로그인한 사용자의 userId를 안전하게 반환
     * 인증되지 않은 경우 "anonymous" 반환
     * @return userId or "anonymous"
     */
    fun getCurrentIdOrAnonymous(): String {
        val authentication = securityExtractor.getAuthentication() ?: return "anonymous"
        return authentication.name ?: "anonymous"
    }

    /**
     * 현재 로그인한 사용자의 role을 안전하게 반환
     * 인증되지 않은 경우 "anonymous" 반환
     * @return role or "anonymous"
     */
    fun getCurrentRoleOrAnonymous(): String {
        val authentication = securityExtractor.getAuthentication() ?: return "anonymous"
        val authority = authentication.authorities.firstOrNull() ?: return "anonymous"
        return authority.authority.removePrefix("ROLE_")
    }

    /**
     * 인증 정보를 모두 삭제하여 로그아웃 처리
     */
    fun clearAuthentication() {
        // JWT 쿠키 삭제
        val response = httpUtil.getResponse()
        httpUtil.clearTokenCookies(response)

        // 보안 컨텍스트 초기화
        securityTransformer.clearContextHolder()
    }
}