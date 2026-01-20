package com.jp.jp.domain.jlptWord.service

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import com.jp.jp.domain.jlptWord.manager.JlptWordManager
import com.jp.jp.domain.jlptWord.service.business.JlptWordServiceExtractor
import com.jp.jp.domain.userWordLearning.manager.UserWordLearningManager
import org.springframework.stereotype.Service

@Service
class JlptWordService(
    private val jlptWordManager: JlptWordManager,
    private val userWordLearningManager: UserWordLearningManager,
    private val jlptWordServiceExtractor: JlptWordServiceExtractor
) {

    // 기존 호환성을 위한 랜덤 단어 반환 (학습 기록 고려 없음)
    fun getRandomWords(level: String, count: Int): List<JlptWordEntity> {
        return jlptWordManager.getRandomWords(level, count)
    }

    // 사용자의 학습 상태를 고려한 JLPT 단어를 반환함 (PENDING 단어 + 새로운 랜덤 단어)
    fun getPersonalizedWords(userId: Long, level: String, count: Int): List<JlptWordEntity> {
        // 1. 사용자의 모든 학습 기록을 조회함
        val userLearningRecords = userWordLearningManager.findAllByUserId(userId)

        // 2. PENDING 상태인 단어들의 ID를 추출함
        val pendingWordIds = jlptWordServiceExtractor.extractPendingWordIds(userLearningRecords)

        // 3. PENDING 상태인 단어들을 가져옴
        val pendingWords = jlptWordManager.getWordsByIdsAndLevel(pendingWordIds, level)

        // 4. 학습 중인 모든 단어 ID (제외할 단어들)
        val excludeWordIds = jlptWordServiceExtractor.extractAllLearningWordIds(userLearningRecords)

        // 5. 새로운 랜덤 단어를 가져옴 (이미 학습 중인 단어는 제외)
        val randomWords = (count - pendingWords.size)
            .takeIf { it > 0 }
            ?.let { jlptWordManager.getRandomWordsExcluding(level, it, excludeWordIds) }
            ?: emptyList()

        // 6. PENDING 단어와 새로운 랜덤 단어를 섞어서 반환
        return (pendingWords + randomWords).shuffled().take(count)
    }
}