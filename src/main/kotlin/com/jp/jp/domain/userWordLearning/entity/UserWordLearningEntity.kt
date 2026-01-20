package com.jp.jp.domain.userWordLearning.entity

import com.jp.jp.util.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_word_learning")
class UserWordLearningEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // 기본 정보
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "word_id", nullable = false)
    val wordId: Long,

    // 학습 성과 추적
    @Column(name = "consecutive_correct", nullable = false)
    val consecutiveCorrect: Int = 0,      // 연속 정답 횟수 (1,2,3,4,5,6+)

    @Column(name = "consecutive_incorrect", nullable = false)
    val consecutiveIncorrect: Int = 0,    // 연속 오답 횟수 (1,2+)

    @Column(name = "total_attempts", nullable = false)
    val totalAttempts: Int = 0,           // 총 시도 횟수

    // 시간 관리
    @Column(name = "last_studied_at")
    val lastStudiedAt: LocalDateTime? = null,     // 마지막 학습 시간

    @Column(name = "next_review_at")
    val nextReviewAt: LocalDateTime? = null,      // 다음 복습 예정 시간

    // 현재 상태
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val status: StudyStatus = StudyStatus.NEW

) : BaseEntity()