package com.jp.jp.config.log

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException
import java.util.*

// 모든 HTTP 요청/응답을 로깅하는 필터 클래스
class LogFilter(
    private val logManager: LogManager
) : OncePerRequestFilter() {

    companion object {
        private const val UUID_KEY = "uuid"
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestWrapper = CachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)
        val startTime = System.currentTimeMillis()
        val uri = requestWrapper.requestURI

        val uuid = UUID.randomUUID().toString()

        try {
            MDC.put(UUID_KEY, uuid)
            // 1. 요청 로그 출력
            logManager.printRequest(requestWrapper)
            filterChain.doFilter(requestWrapper, responseWrapper)
        } finally {
            // 2. 응답 로그 출력
            logManager.printResponse(responseWrapper, startTime, uri)

            responseWrapper.copyBodyToResponse()
            MDC.clear()
        }
    }
}