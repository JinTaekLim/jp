package com.jp.jp.domain.userWordLearning.service

import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import com.jp.jp.domain.userWordLearning.manager.UserWordLearningManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserWordLearningService(
    private val userWordLearningManager: UserWordLearningManager
) {

    // 단어 학습 성공을 기록함 (오답 카운트 초기화 및 간격 반복 알고리즘 적용)
    @Transactional
    fun recordWordStudySuccess(userId: Long, wordId: Long): UserWordLearningEntity {
        return userWordLearningManager.recordSuccess(userId, wordId)
    }

    // 단어 학습 실패를 기록함 (정답 카운트 초기화 및 간격 반복 알고리즘 적용)
    @Transactional
    fun recordWordStudyFailure(userId: Long, wordId: Long): UserWordLearningEntity {
        return userWordLearningManager.recordFailure(userId, wordId)
    }
}