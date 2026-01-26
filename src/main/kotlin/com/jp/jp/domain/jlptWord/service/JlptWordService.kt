package com.jp.jp.domain.jlptWord.service

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import com.jp.jp.domain.jlptWord.manager.JlptWordManager
import com.jp.jp.domain.jlptWord.service.business.JlptWordServiceCalculate
import com.jp.jp.domain.jlptWord.service.business.JlptWordServiceExtractor
import com.jp.jp.domain.userJlptLevel.entity.JlptLevel
import com.jp.jp.domain.userWordLearning.manager.UserWordLearningManager
import org.springframework.stereotype.Service

@Service
class JlptWordService(
    private val jlptWordManager: JlptWordManager,
    private val userWordLearningManager: UserWordLearningManager,
    private val jlptWordServiceExtractor: JlptWordServiceExtractor,
    private val jlptWordServiceCalculate: JlptWordServiceCalculate
) {

    // 기존 호환성을 위한 랜덤 단어 반환 (학습 기록 고려 없음)
    fun getRandomWords(level: JlptLevel, count: Int): List<JlptWordEntity> {
        return jlptWordManager.getRandomWords(level, count)
    }

    // 복습 필요한 단어와 새로운 단어를 적응적 비율로 반환함 (LEARNING 상태는 제외)
    fun getPersonalizedWords(userId: Long, level: JlptLevel, count: Int): List<JlptWordEntity> {
        // 1. 사용자의 모든 학습 기록을 조회함
        val userLearningRecords = userWordLearningManager.findAllByUserId(userId)

        // 2. 복습이 필요한 단어 ID들을 추출함 (NEEDS_REVIEW 상태)
        val urgentReviewWordIds = jlptWordServiceExtractor.extractUrgentReviewWordIds(userLearningRecords)

        // 3. 적응적 복습 개수 계산: 복습 상황에 따라 동적 조절
        val reviewAllocation = jlptWordServiceExtractor.calculateAdaptiveReviewCount(urgentReviewWordIds.size, count)
        val newWordAllocation = count - reviewAllocation

        val result = mutableListOf<JlptWordEntity>()

        // 4. 복습 필요한 단어들 추가 (적응적 비율)
        if (reviewAllocation > 0) {
            val urgentWords = jlptWordManager.getWordsByIdsAndLevel(urgentReviewWordIds, level)
                .shuffled()
                .take(reviewAllocation)
            result.addAll(urgentWords)
        }

        // 5. 새로운 단어들 추가 (적응적 비율)
        if (newWordAllocation > 0) {
            val excludeWordIds = jlptWordServiceExtractor.extractAllLearningWordIds(userLearningRecords)
            val randomWords = jlptWordManager.getRandomWordsExcluding(level, newWordAllocation, excludeWordIds)
            result.addAll(randomWords)
        }

        return result.toList()
    }

    // 목표 레벨에 따라 하위 레벨들에서 균등하게 단어를 반환함 (Manager 위임)
    fun getBalancedWordsByTargetLevel(targetLevel: JlptLevel, count: Int): List<JlptWordEntity> {
        // 1. 목표 레벨에 포함되는 모든 레벨 계산 (N2면 [N5, N4, N3, N2])
        val includedLevels = JlptLevel.getIncludedLevels(targetLevel)

        // 2. 각 레벨별 할당 개수 계산
        val levelCounts = jlptWordServiceCalculate.calculateLevelCounts(includedLevels, count)

        // 3. Manager에서 병렬 조회 처리
        return jlptWordManager.getRandomWordsByLevels(levelCounts)
    }
}