package com.jp.jp.util.jwt

import com.jp.jp.domain.users.entity.UserRole
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Component

@Component
class TokenExtractor {

    /**
     * Claims에서 사용자 ID 추출
     */
    fun extractUserId(claims: Claims): Long {
        return claims["userId"]?.toString()?.toLong()
            ?: throw IllegalArgumentException("UserId not found in JWT token")
    }

    /**
     * Claims에서 사용자 타입 추출
     */
    fun extractUserType(claims: Claims): UserRole {
        val role = claims["role"]?.toString()
            ?: throw IllegalArgumentException("Role not found in JWT token")

        return try {
            UserRole.valueOf(role)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid role in JWT token: $role")
        }
    }
}