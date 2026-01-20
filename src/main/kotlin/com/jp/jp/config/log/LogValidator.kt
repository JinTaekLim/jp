package com.jp.jp.config.log

import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher

// 로그 처리 여부를 검증하는 클래스
@Component
class LogValidator {

    private val ignoreUri = arrayOf(
        "/actuator/**",
        "/css/**",
        "/js/**",
        "/images/**",
        "/favicon.ico",
        "/error"
    )

    private val pathMatcher = AntPathMatcher()

    // 로그에서 제외할 URI인지 확인
    fun isIgnoredUri(uri: String): Boolean {
        return ignoreUri.any { pattern ->
            pathMatcher.match(pattern, uri)
        }
    }

    // 로그 가능한 URI인지 검증 (필요시 사용)
    fun validateLoggableUri(uri: String) {
        val isIgnored = ignoreUri.any { pattern ->
            pathMatcher.match(pattern, uri)
        }

        require(!isIgnored) {
            "Ignored URI: $uri"
        }
    }
}