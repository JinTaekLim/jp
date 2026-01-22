package com.jp.jp.domain.users.service

import com.jp.jp.util.auth.AuthManager
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authManager: AuthManager
) {

    // 현재 로그인한 사용자의 ID를 반환함
    fun getCurrentUserId(): Long {
        return authManager.getCurrentUserId()
    }

    // 현재 로그인한 사용자의 ID를 반환하고 로그인되지 않았으면 null 반환
    fun getCurrentUserIdOrNull(): Long? {
        return try {
            authManager.getCurrentUserId()
        } catch (e: Exception) {
            null
        }
    }

    // 로그아웃 처리 - 인증 정보 삭제
    fun logout() {
        authManager.clearAuthentication()
    }
}