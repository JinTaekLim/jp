package com.jp.jp.util

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * 비밀번호 암호화 및 검증을 담당하는 클래스
 */
@Component
class PasswordEncoderManager(
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * 평문 비밀번호를 암호화함
     */
    fun encode(rawPassword: String): String {
        return passwordEncoder.encode(rawPassword)
    }

    /**
     * 평문 비밀번호와 암호화된 비밀번호가 일치하는지 확인함
     */
    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

    /**
     * 비밀번호가 일치하지 않으면 예외를 발생시킴
     */
    fun matchesOrThrow(rawPassword: String, encodedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다")
        }
    }
}