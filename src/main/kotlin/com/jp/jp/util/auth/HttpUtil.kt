package com.jp.jp.util.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Duration

/**
 * HTTP 관련 유틸리티 클래스
 */
@Component
class HttpUtil(
    @Value("\${cookie.secure:false}") private val cookieSecure: Boolean,
    @Value("\${cookie.sameSite:Lax}") private val cookieSameSite: String
) {

    companion object {
        const val ACCESS_TOKEN_COOKIE = "access_token"
        const val REFRESH_TOKEN_COOKIE = "refresh_token"
        const val DEFAULT_PATH = "/"
        const val DEFAULT_MAX_AGE = 1800 // 30분
        const val REFRESH_MAX_AGE = 604800 // 7일

        // JWT 관련 상수
        const val HEADER_STRING = "Authorization"
        const val TOKEN_PREFIX = "Bearer "
    }

    /**
     * HTTP 응답 객체 가져오기
     */
    fun getResponse(): HttpServletResponse {
        val requestAttributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        return requestAttributes.response
            ?: throw IllegalStateException("HTTP 응답 객체를 가져올 수 없습니다.")
    }

    /**
     * 쿠키 설정 (보안 쿠키 - ResponseCookie 사용)
     */
    fun setSecureCookie(
        response: HttpServletResponse,
        name: String,
        value: String,
        maxAge: Int = DEFAULT_MAX_AGE,
        path: String = DEFAULT_PATH,
        secure: Boolean = cookieSecure,
        sameSite: String = cookieSameSite
    ) {
        val cookie = ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(secure)
            .path(path)
            .maxAge(Duration.ofSeconds(maxAge.toLong()))
            .sameSite(sameSite)
            .build()

        response.addHeader("Set-Cookie", cookie.toString())
    }

    /**
     * 액세스 토큰 쿠키 설정
     */
    fun setAccessTokenCookie(response: HttpServletResponse, token: String) {
        setSecureCookie(response, ACCESS_TOKEN_COOKIE, token, DEFAULT_MAX_AGE)
    }

    /**
     * 리프레시 토큰 쿠키 설정
     */
    fun setRefreshTokenCookie(response: HttpServletResponse, token: String) {
        setSecureCookie(response, REFRESH_TOKEN_COOKIE, token, REFRESH_MAX_AGE)
    }

    /**
     * 쿠키 삭제 (ResponseCookie 사용)
     */
    fun clearCookie(response: HttpServletResponse, name: String, path: String = DEFAULT_PATH) {
        val cookie = ResponseCookie.from(name, "")
            .httpOnly(true)
            .secure(cookieSecure)
            .path(path)
            .maxAge(Duration.ZERO)
            .sameSite(cookieSameSite)
            .build()

        response.addHeader("Set-Cookie", cookie.toString())
    }

    /**
     * 토큰 쿠키들 삭제
     */
    fun clearTokenCookies(response: HttpServletResponse) {
        clearCookie(response, ACCESS_TOKEN_COOKIE)
        clearCookie(response, REFRESH_TOKEN_COOKIE)
    }

    /**
     * JWT 토큰을 Authorization 헤더에 설정
     */
    fun setJwtTokenHeader(response: HttpServletResponse, token: String) {
        response.setHeader(HEADER_STRING, TOKEN_PREFIX + token)
    }

    /**
     * Authorization 헤더에서 JWT 토큰 추출
     */
    fun extractTokenFromRequest(request: HttpServletRequest): String? {
        return request.getHeader(HEADER_STRING)
            ?.takeIf { it.startsWith(TOKEN_PREFIX) }
            ?.substring(TOKEN_PREFIX.length)
    }

    /**
     * 쿠키에서 액세스 토큰 추출
     */
    fun getAccessTokenFromCookie(request: HttpServletRequest): String? {
        val cookies = request.cookies ?: return null
        return cookies.firstOrNull { it.name == ACCESS_TOKEN_COOKIE }?.value
    }

    /**
     * 쿠키에서 리프레시 토큰 추출
     */
    fun getRefreshTokenFromCookie(request: HttpServletRequest): String {
        return request.cookies
            ?.firstOrNull { it.name == REFRESH_TOKEN_COOKIE }
            ?.value
            ?: throw IllegalArgumentException("리프레시 토큰이 쿠키에 존재하지 않습니다.")
    }
}