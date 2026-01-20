package com.jp.jp.domain.users.repository

import com.jp.jp.domain.users.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {

    // 이메일로 사용자 조회 (로그인용, 없으면 null 반환)
    fun findByEmail(email: String): UserEntity?

    // 이메일로 활성화된 사용자만 조회 (로그인용, 없으면 null 반환)
    fun findByEmailAndIsActive(email: String, isActive: Boolean): UserEntity?

    // 이메일 중복 확인 (회원가입 시 사용)
    fun existsByEmail(email: String): Boolean
}