package com.jp.jp.domain.userWordLearning.repository

import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserWordLearningRepository : JpaRepository<UserWordLearningEntity, Long> {

    // 특정 사용자의 특정 단어 학습 상태를 조회함
    fun findByUserIdAndWordId(userId: Long, wordId: Long): UserWordLearningEntity?
}