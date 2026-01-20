package com.jp.jp.domain.userJlptLevel.entity

import com.jp.jp.util.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_jlpt_levels")
class UserJlptLevelEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // 사용자 ID
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    // 목표 JLPT 레벨
    @Column(name = "target_level", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    val targetLevel: JlptLevel
) : BaseEntity()