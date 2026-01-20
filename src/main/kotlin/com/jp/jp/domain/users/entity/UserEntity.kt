package com.jp.jp.domain.users.entity

import com.jp.jp.util.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // 사용자 이메일 (로그인 ID로 사용)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    val email: String,

    // 사용자 이름
    @Column(name = "name", nullable = false, length = 50)
    val name: String,

    // 비밀번호 (암호화된 상태로 저장)
    @Column(name = "password", nullable = false, length = 255)
    val password: String,

    // 계정 활성화 여부
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,


    // 사용자 역할 (USER, ADMIN 등)
    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER
) : BaseEntity()