package com.jp.jp.domain.jlptWord.service.business

import com.jp.jp.domain.userWordLearning.entity.StudyStatus
import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import org.springframework.stereotype.Component

@Component
class JlptWordServiceExtractor {

    // 복습이 긴급한 단어 ID들을 추출함 (NEEDS_REVIEW 상태 = 이미 복습 시간 도달)
    fun extractUrgentReviewWordIds(userLearningRecords: List<UserWordLearningEntity>): Set<Long> {
        return userLearningRecords
            .filter { it.status == StudyStatus.NEEDS_REVIEW }
            .map { it.wordId }
            .toSet()
    }

    // 학습 중인 모든 단어 ID들을 추출함 (제외할 단어 목록용)
    fun extractAllLearningWordIds(userLearningRecords: List<UserWordLearningEntity>): Set<Long> {
        return userLearningRecords.map { it.wordId }.toSet()
    }

    // 복습 상황에 따른 적응적 복습 개수 계산함 (고정 70:30에서 동적 조절로 변경)
    fun calculateAdaptiveReviewCount(reviewCount: Int, requestCount: Int): Int {
        return when {
            reviewCount == 0 -> 0  // 복습 없음 → 복습 0개
            reviewCount >= requestCount * 2 -> requestCount  // 복습 과부하 → 100% 복습
            reviewCount >= requestCount -> (requestCount * 0.8).toInt()  // 복습 80%
            else -> reviewCount  // 모든 복습 처리
        }
    }

}