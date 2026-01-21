package com.jp.jp.domain.userWordLearning.manager.business

import com.jp.jp.domain.userWordLearning.entity.StudyStatus
import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserWordLearningManagerMapper {

    // 새로운 단어 학습 엔티티를 생성함 (첫 학습)
    fun createNewWordLearningEntity(
        userId: Long,
        wordId: Long,
        isSuccess: Boolean,
        nextReviewAt: LocalDateTime
    ): UserWordLearningEntity {
        return UserWordLearningEntity(
            userId = userId,
            wordId = wordId,
            consecutiveCorrect = if (isSuccess) 1 else 0,
            consecutiveIncorrect = if (isSuccess) 0 else 1,
            totalAttempts = 1,
            lastStudiedAt = LocalDateTime.now(),
            nextReviewAt = nextReviewAt,
            status = StudyStatus.LEARNING
        )
    }

    // 기존 단어 학습 엔티티를 업데이트함 (성공 처리)
    fun updateEntityForSuccess(
        entity: UserWordLearningEntity,
        nextReviewAt: LocalDateTime
    ): UserWordLearningEntity {
        return UserWordLearningEntity(
            id = entity.id,
            userId = entity.userId,
            wordId = entity.wordId,
            consecutiveCorrect = entity.consecutiveCorrect + 1,
            consecutiveIncorrect = 0, // 성공이므로 오답 카운트 초기화
            totalAttempts = entity.totalAttempts + 1,
            lastStudiedAt = LocalDateTime.now(),
            nextReviewAt = nextReviewAt,
            status = StudyStatus.LEARNING
        )
    }

    // 기존 단어 학습 엔티티를 업데이트함 (실패 처리)
    fun updateEntityForFailure(
        entity: UserWordLearningEntity,
        nextReviewAt: LocalDateTime
    ): UserWordLearningEntity {
        val newConsecutiveIncorrect = entity.consecutiveIncorrect + 1

        return UserWordLearningEntity(
            id = entity.id,
            userId = entity.userId,
            wordId = entity.wordId,
            consecutiveCorrect = 0, // 실패이므로 정답 카운트 초기화
            consecutiveIncorrect = newConsecutiveIncorrect,
            totalAttempts = entity.totalAttempts + 1,
            lastStudiedAt = LocalDateTime.now(),
            nextReviewAt = nextReviewAt,
            status = StudyStatus.LEARNING
        )
    }

}