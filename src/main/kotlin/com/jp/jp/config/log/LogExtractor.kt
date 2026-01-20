package com.jp.jp.config.log

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import java.io.InputStream
import java.nio.charset.StandardCharsets

// HTTP 요청에서 로그에 필요한 정보를 추출하는 클래스
@Component
class LogExtractor {

    // URI와 쿼리 스트링을 조합하여 반환
    fun getUriPlusQueryString(request: HttpServletRequest): String {
        val queryString = request.queryString
        val uri = request.requestURI
        val uriPlusQueryString = "$uri?$queryString"

        return if (queryString == null) uri else uriPlusQueryString
    }

    // InputStream에서 본문 내용을 문자열로 추출
    fun getBody(inputStream: InputStream): String? {
        val content = StreamUtils.copyToByteArray(inputStream)

        if (content.isEmpty()) return null

        return String(content, StandardCharsets.UTF_8)
    }

    // 클라이언트 IP 주소를 추출 (프록시 환경 고려)
    fun getClientIp(request: HttpServletRequest): String {
        val clientIp = request.getHeader("X-Forwarded-For")

        return clientIp?.takeIf { it.isNotBlank() } ?: request.remoteAddr
    }

    // User-Agent 헤더 추출
    fun getUserAgent(request: HttpServletRequest): String? {
        return request.getHeader("User-Agent")
    }

    // 쿼리 파라미터 추출
    fun getQueryParameters(request: HttpServletRequest): String? {
        return request.queryString
    }
}