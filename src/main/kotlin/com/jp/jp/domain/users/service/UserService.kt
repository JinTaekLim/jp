package com.jp.jp.domain.users.service

import com.jp.jp.domain.users.dto.*
import com.jp.jp.domain.users.entity.UserRole
import com.jp.jp.domain.users.manager.UserManager
import com.jp.jp.domain.users.service.business.UserServiceMapper
import com.jp.jp.util.PasswordEncoderManager
import com.jp.jp.util.auth.AuthProvider
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userManager: UserManager,
    private val passwordEncoderManager: PasswordEncoderManager,
    private val authProvider: AuthProvider,
    private val userServiceMapper: UserServiceMapper
) {

    // 회원가입 처리
    fun register(request: RegisterRequest) {
        // 이메일 중복 확인
        userManager.existsByEmailOrThrow(request.email)

        // 비밀번호 암호화
        val encodedPassword = passwordEncoderManager.encode(request.password)

        // 사용자 엔티티 생성
        val user = userServiceMapper.toEntity(request, encodedPassword)

        // 사용자 저장
        userManager.save(user)
    }

    // 로그인 처리 및 쿠키에 토큰 설정
    fun login(request: LoginRequest) {
        // 활성화된 사용자 조회
        val user = userManager.findActiveByEmailOrThrow(request.email)

        // 비밀번호 확인
        passwordEncoderManager.matchesOrThrow(request.password, user.password)

        // AuthProvider를 통해 토큰 발급 및 쿠키 설정
        authProvider.issueTokensAndSetCookies(user.id!!, user.role)
    }

    // 현재 로그인한 사용자 정보 조회 (로그인되지 않은 경우 GUEST 정보 반환)
    fun getCurrentUser(userId: Long?): CurrentUserResponse {
        return userId?.let {
            userServiceMapper.toCurrentUserResponse(userManager.findByIdOrThrow(it))
        } ?: userServiceMapper.toGuestResponse()
    }

    // 로그아웃 처리
    fun logout() {
        // AuthProvider를 통해 인증 정보 삭제
        authProvider.logout()
    }

}