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
}