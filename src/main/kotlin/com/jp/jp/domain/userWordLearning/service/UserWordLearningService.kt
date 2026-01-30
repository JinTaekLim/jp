package com.jp.jp.domain.userWordLearning.service

import com.jp.jp.domain.jlptWord.manager.JlptWordManager
import com.jp.jp.domain.userWordLearning.dto.StudiedWordsResponse
import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import com.jp.jp.domain.userWordLearning.manager.UserWordLearningManager
import com.jp.jp.domain.userWordLearning.service.business.UserWordLearningServiceMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserWordLearningService(
    private val userWordLearningManager: UserWordLearningManager,
    private val jlptWordManager: JlptWordManager,
    private val userWordLearningServiceMapper: UserWordLearningServiceMapper
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

    // 사용자가 학습한 단어 목록을 lastId 기반으로 조회함 (무한스크롤)
    @Transactional(readOnly = true)
    fun  getStudiedWords(userId: Long, lastId: Long?, size: Int): StudiedWordsResponse {
        val studyRecords = userWordLearningManager.findUserStudiedWords(userId, lastId, size)

        // 다음 페이지 존재 여부 확인 (size + 1로 조회했으므로)
        val hasNext = studyRecords.size > size
        val actualRecords = if (hasNext) studyRecords.take(size) else studyRecords

        // 단어 정보 조회 및 응답 DTO 생성 (중복 wordId 제거)
        val wordIds = actualRecords.map { it.wordId }.distinct()
        val jlptWords = jlptWordManager.getWordsByIds(wordIds)
        val studiedWordResponses = userWordLearningServiceMapper.toStudiedWordResponses(actualRecords, jlptWords)

        return userWordLearningServiceMapper.toStudiedWordsResponse(
            studiedWordResponses,
            actualRecords,
            size,
            hasNext
        )
    }

    // 사용자가 학습한 총 단어 수를 조회함
    @Transactional(readOnly = true)
    fun getTotalStudiedWordsCount(userId: Long): Long {
        return userWordLearningManager.countByUserId(userId)
    }
}