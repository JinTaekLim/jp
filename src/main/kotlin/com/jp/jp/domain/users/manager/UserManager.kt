package com.jp.jp.domain.users.manager

import com.jp.jp.domain.users.entity.UserEntity
import com.jp.jp.domain.users.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class UserManager(
    private val userRepository: UserRepository
) {

    // 사용자를 저장하고 결과를 반환함
    fun save(user: UserEntity): UserEntity {
        return userRepository.save(user)
    }

    // ID로 사용자를 조회하고 없으면 예외를 발생시킴
    fun findByIdOrThrow(id: Long): UserEntity {
        return userRepository.findById(id).orElseThrow {
            IllegalArgumentException("사용자를 찾을 수 없습니다: $id")
        }
    }

    // 이메일로 사용자를 조회하고 없으면 null을 반환함
    fun findByEmailOrNull(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }

    // 이메일로 활성화된 사용자를 조회하고 없으면 예외를 발생시킨다
    fun findActiveByEmailOrThrow(email: String): UserEntity {
        return userRepository.findByEmailAndIsActive(email, true)
            ?: throw IllegalArgumentException("존재하지 않거나 비활성화된 사용자입니다: $email")
    }

    // 이메일 중복 여부를 확인함
    fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    // 이메일 중복 여부를 확인하고 중복이면 예외를 발생시킴
    fun existsByEmailOrThrow(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다: $email")
        }
    }
}