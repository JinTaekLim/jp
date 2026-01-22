package com.jp.jp.domain.userWordLearning.manager

import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import com.jp.jp.domain.userWordLearning.manager.business.UserWordLearningManagerCalculator
import com.jp.jp.domain.userWordLearning.manager.business.UserWordLearningManagerMapper
import com.jp.jp.domain.userWordLearning.repository.UserWordLearningRepository
import org.springframework.stereotype.Component

@Component
class UserWordLearningManager(
    private val userWordLearningRepository: UserWordLearningRepository,
    private val userWordLearningManagerMapper: UserWordLearningManagerMapper,
    private val userWordLearningManagerCalculator: UserWordLearningManagerCalculator
) {

    // 특정 사용자의 특정 단어 학습 상태를 조회함 (없으면 null 반환)
    fun findByUserIdAndWordIdOrNull(userId: Long, wordId: Long): UserWordLearningEntity? {
        return userWordLearningRepository.findByUserIdAndWordId(userId, wordId)
    }

    // 특정 사용자의 모든 단어 학습 기록을 조회함
    fun findAllByUserId(userId: Long): List<UserWordLearningEntity> {
        return userWordLearningRepository.findAllByUserId(userId)
    }

    // 단어 학습 상태를 저장하거나 업데이트함
    fun save(entity: UserWordLearningEntity): UserWordLearningEntity {
        return userWordLearningRepository.save(entity)
    }

    // 사용자의 단어 학습 성공 처리 (오답 카운트 초기화)
    fun recordSuccess(userId: Long, wordId: Long): UserWordLearningEntity {
        val entity = findByUserIdAndWordIdOrNull(userId, wordId)

        return if (entity == null) {
            // 처음 학습하는 단어인 경우
            val nextReviewAt = userWordLearningManagerCalculator.calculateNextReviewTime(1, true, false)
            val newEntity = userWordLearningManagerMapper.createNewWordLearningEntity(
                userId = userId,
                wordId = wordId,
                isSuccess = true,
                nextReviewAt = nextReviewAt
            )
            save(newEntity)
        } else {
            // 기존 학습 기록이 있는 경우
            val newConsecutiveCorrect = entity.consecutiveCorrect + 1
            val hadFailures = entity.consecutiveIncorrect > 0
            val nextReviewAt = userWordLearningManagerCalculator.calculateNextReviewTime(newConsecutiveCorrect, true, hadFailures)

            val updatedEntity = userWordLearningManagerMapper.updateEntityForSuccess(
                entity = entity,
                nextReviewAt = nextReviewAt
            )
            save(updatedEntity)
        }
    }

    // 사용자의 단어 학습 실패 처리 (정답 카운트 초기화)
    fun recordFailure(userId: Long, wordId: Long): UserWordLearningEntity {
        val entity = findByUserIdAndWordIdOrNull(userId, wordId)

        return if (entity == null) {
            // 처음 학습하는 단어인 경우
            val nextReviewAt = userWordLearningManagerCalculator.calculateNextReviewTime(1, false, false)
            val newEntity = userWordLearningManagerMapper.createNewWordLearningEntity(
                userId = userId,
                wordId = wordId,
                isSuccess = false,
                nextReviewAt = nextReviewAt
            )
            save(newEntity)
        } else {
            // 기존 학습 기록이 있는 경우
            val newConsecutiveIncorrect = entity.consecutiveIncorrect + 1
            val nextReviewAt = userWordLearningManagerCalculator.calculateNextReviewTime(newConsecutiveIncorrect, false, false)

            val updatedEntity = userWordLearningManagerMapper.updateEntityForFailure(
                entity = entity,
                nextReviewAt = nextReviewAt
            )
            save(updatedEntity)
        }
    }

    // lastId 기반으로 사용자의 학습한 단어들을 조회함
    fun findUserStudiedWords(
        userId: Long,
        lastId: Long?,
        size: Int
    ): List<UserWordLearningEntity> {
        return userWordLearningRepository.findUserStudiedWords(userId, lastId, size)
    }

}