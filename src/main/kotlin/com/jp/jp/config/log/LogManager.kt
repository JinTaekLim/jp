package com.jp.jp.config.log

import com.jp.jp.util.auth.AuthManager
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingResponseWrapper

// 로그 관리를 담당하는 메인 클래스
@Component
class LogManager(
    private val logPrinter: LogPrinter,
    private val logExtractor: LogExtractor,
    private val logValidator: LogValidator,
    private val authManager: AuthManager
) {

    // 요청 로그를 처리하는 메소드
    fun printRequest(request: HttpServletRequest) {
        val uri = request.requestURI

        uri.takeUnless(logValidator::isIgnoredUri)?.let {
            val userId = authManager.getCurrentIdOrAnonymous()
            val role = authManager.getCurrentRoleOrAnonymous()

            logPrinter.printRequestLog(
                request.method,
                logExtractor.getUriPlusQueryString(request),
                request.contentType,
                logExtractor.getBody(request.inputStream),
                logExtractor.getClientIp(request),
                userId,
                role
            )
        }
    }

    // 응답 로그를 처리하는 메소드
    fun printResponse(response: ContentCachingResponseWrapper, startTime: Long, uri: String) {
        val body = logExtractor.getBody(response.contentInputStream)

        uri.takeUnless(logValidator::isIgnoredUri)?.let {
            logPrinter.printResponseLog(response.status, body, startTime)
        }
    }
}