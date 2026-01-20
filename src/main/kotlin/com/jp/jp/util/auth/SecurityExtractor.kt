package com.jp.jp.util.auth

import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class SecurityExtractor {

    fun getAuthentication(): Authentication? {
        return SecurityContextHolder.getContext()?.authentication
    }

    fun isAuthenticated(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication != null && authentication.isAuthenticated
    }

    fun getResponse(): HttpServletResponse {
        val requestAttributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        return requestAttributes.response!!
    }
}