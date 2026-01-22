package com.jp.jp.domain.users.dto

// 현재 로그인한 사용자 정보 응답 DTO
data class CurrentUserResponse(
    val name: String,           // 사용자 이름 (GUEST 또는 실제 이름)
    val email: String,          // 사용자 이메일 (GUEST 또는 실제 이메일)
    val role: String            // 사용자 역할 (USER, ADMIN 또는 GUEST)
)