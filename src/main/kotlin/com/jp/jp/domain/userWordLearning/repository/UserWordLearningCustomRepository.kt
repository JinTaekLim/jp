package com.jp.jp.domain.userWordLearning.repository

import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity

// QueryDSL을 활용한 커스텀 Repository 인터페이스
interface UserWordLearningCustomRepository {

    // lastId 기반으로 사용자의 학습한 단어들을 조회함 (최근 학습 순으로 정렬)
    fun findUserStudiedWords(
        userId: Long,
        lastId: Long?,
        size: Int
    ): List<UserWordLearningEntity>
}