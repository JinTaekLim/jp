package com.jp.jp.util.auth

import com.jp.jp.domain.users.entity.UserRole
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityTransformer {

    fun setContextHolder(userId: Long, userType: UserRole) {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${userType}"))
        val authentication = UsernamePasswordAuthenticationToken(userId.toString(), null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }

    fun setContextHolder(userId: String, userType: String) {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${userType}"))
        val authentication = UsernamePasswordAuthenticationToken(userId, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }

    fun clearContextHolder() {
        SecurityContextHolder.clearContext()
    }
}