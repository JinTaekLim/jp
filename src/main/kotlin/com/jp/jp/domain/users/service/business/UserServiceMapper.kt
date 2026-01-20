package com.jp.jp.domain.users.service.business

import com.jp.jp.domain.users.dto.RegisterRequest
import com.jp.jp.domain.users.entity.UserEntity
import com.jp.jp.domain.users.entity.UserRole
import org.springframework.stereotype.Component

@Component
class UserServiceMapper {

    // 회원가입 요청을 UserEntity로 변환함
    fun toEntity(request: RegisterRequest, encodedPassword: String): UserEntity {
        return UserEntity(
            email = request.email,
            name = request.name,
            password = encodedPassword,
            role = UserRole.USER
        )
    }
}