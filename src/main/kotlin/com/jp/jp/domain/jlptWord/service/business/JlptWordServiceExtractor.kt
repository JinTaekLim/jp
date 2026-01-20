package com.jp.jp.domain.jlptWord.service.business

import com.jp.jp.domain.userWordLearning.entity.StudyStatus
import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import org.springframework.stereotype.Component

@Component
class JlptWordServiceExtractor {

    // PENDING 상태인 단어 ID들을 추출함
    fun extractPendingWordIds(userLearningRecords: List<UserWordLearningEntity>): Set<Long> {
        return userLearningRecords
            .filter { it.status == StudyStatus.PENDING }
            .map { it.wordId }
            .toSet()
    }

    // 학습 중인 모든 단어 ID들을 추출함 (제외할 단어 목록용)
    fun extractAllLearningWordIds(userLearningRecords: List<UserWordLearningEntity>): Set<Long> {
        return userLearningRecords.map { it.wordId }.toSet()
    }

}