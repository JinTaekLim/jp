package com.jp.jp.domain.userWordLearning.scheduler

import com.jp.jp.domain.userWordLearning.entity.StudyStatus
import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import com.jp.jp.domain.userWordLearning.repository.UserWordLearningRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class ReviewStatusScheduler(
    private val userWordLearningRepository: UserWordLearningRepository
) {

    private val log = LoggerFactory.getLogger(ReviewStatusScheduler::class.java)

    // 15분마다 복습 시간이 도래한 LEARNING 단어들을 NEEDS_REVIEW로 변경함
    @Scheduled(fixedDelay = 900000) // 15분 = 900,000ms
    @Transactional
    fun updateExpiredReviewsToNeedsReview() {
        try {
            val now = LocalDateTime.now()

            // 복습 시간이 지난 LEARNING 상태의 단어들을 조회함
            val expiredLearningWords = userWordLearningRepository
                .findByStatusAndNextReviewAtBefore(StudyStatus.LEARNING, now)

            if (expiredLearningWords.isNotEmpty()) {
                // NEEDS_REVIEW 상태로 업데이트
                val updatedWords = expiredLearningWords.map { entity ->
                    UserWordLearningEntity(
                        id = entity.id,
                        userId = entity.userId,
                        wordId = entity.wordId,
                        consecutiveCorrect = entity.consecutiveCorrect,
                        consecutiveIncorrect = entity.consecutiveIncorrect,
                        totalAttempts = entity.totalAttempts,
                        lastStudiedAt = entity.lastStudiedAt,
                        nextReviewAt = entity.nextReviewAt,
                        status = StudyStatus.NEEDS_REVIEW
                    )
                }

                userWordLearningRepository.saveAll(updatedWords)

                log.info("스케줄러: ${updatedWords.size}개 단어의 상태를 LEARNING → NEEDS_REVIEW로 업데이트했습니다.")
            }

        } catch (e: Exception) {
            log.error("복습 상태 업데이트 스케줄러 실행 중 오류 발생", e)
        }
    }
}