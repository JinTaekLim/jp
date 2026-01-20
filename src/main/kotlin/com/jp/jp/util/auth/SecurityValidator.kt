package com.jp.jp.util.auth

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class SecurityValidator {

    companion object {
        private const val GUEST = "anonymousUser"
    }

    fun validateAndGetUserId(authentication: Authentication?) {
        if (authentication == null) {
            throw IllegalArgumentException("Authentication is null")
        }

        if (authentication.principal == GUEST) {
            throw IllegalArgumentException("Guest user not allowed")
        }

        val name = authentication.name
        if (name.isNullOrBlank()) {
            throw IllegalArgumentException("Authentication name is null or empty")
        }
    }
}