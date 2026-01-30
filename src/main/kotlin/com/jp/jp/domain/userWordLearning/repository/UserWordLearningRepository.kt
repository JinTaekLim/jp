package com.jp.jp.domain.userWordLearning.repository

import com.jp.jp.domain.userWordLearning.entity.StudyStatus
import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserWordLearningRepository : JpaRepository<UserWordLearningEntity, Long>, UserWordLearningCustomRepository {

    // 특정 사용자의 특정 단어 학습 상태를 조회함
    fun findByUserIdAndWordId(userId: Long, wordId: Long): UserWordLearningEntity?

    // 특정 사용자의 모든 단어 학습 기록을 조회함
    fun findAllByUserId(userId: Long): List<UserWordLearningEntity>

    // 특정 상태이면서 복습 시간이 지난 단어들을 조회함 (스케줄러용)
    fun findByStatusAndNextReviewAtBefore(status: StudyStatus, dateTime: LocalDateTime): List<UserWordLearningEntity>

    // 특정 사용자가 학습한 총 단어 수를 반환함
    fun countByUserId(userId: Long): Long
}