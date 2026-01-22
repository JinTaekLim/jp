package com.jp.jp.domain.userWordLearning.service.business

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import com.jp.jp.domain.userWordLearning.dto.StudiedWordResponse
import com.jp.jp.domain.userWordLearning.dto.StudiedWordsResponse
import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class UserWordLearningServiceMapper {

    // UserWordLearningEntity와 JlptWordEntity를 StudiedWordResponse로 매핑함
    fun toStudiedWordResponse(
        userWordLearning: UserWordLearningEntity,
        jlptWord: JlptWordEntity
    ): StudiedWordResponse {
        // 각각의 메소드로 남은 시간 계산
        val remainingDays = calculateRemainingDays(userWordLearning.nextReviewAt)
        val remainingHours = calculateRemainingHours(userWordLearning.nextReviewAt)
        val remainingMinutes = calculateRemainingMinutes(userWordLearning.nextReviewAt)

        return StudiedWordResponse(
            wordId = userWordLearning.wordId,
            japanese = jlptWord.japanese,
            kanji = jlptWord.kanji,
            meanings = jlptWord.meanings.distinct(),
            partOfSpeech = jlptWord.partOfSpeech,
            level = jlptWord.level,
            totalAttempts = userWordLearning.totalAttempts,
            nextReviewAt = userWordLearning.nextReviewAt,
            remainingDays = remainingDays,
            remainingHours = remainingHours,
            remainingMinutes = remainingMinutes
        )
    }

    // 다음 복습까지 남은 일 수를 계산함
    private fun calculateRemainingDays(nextReviewAt: LocalDateTime?): Long? {
        if (nextReviewAt == null) return null

        val now = LocalDateTime.now()
        if (nextReviewAt.isBefore(now) || nextReviewAt.isEqual(now)) return 0

        val duration = Duration.between(now, nextReviewAt)
        return duration.toMinutes() / (24 * 60)
    }

    // 다음 복습까지 남은 시간 수를 계산함 (24시간 기준)
    private fun calculateRemainingHours(nextReviewAt: LocalDateTime?): Long? {
        if (nextReviewAt == null) return null

        val now = LocalDateTime.now()
        if (nextReviewAt.isBefore(now) || nextReviewAt.isEqual(now)) return 0

        val duration = Duration.between(now, nextReviewAt)
        return (duration.toMinutes() % (24 * 60)) / 60
    }

    // 다음 복습까지 남은 분 수를 계산함 (60분 기준)
    private fun calculateRemainingMinutes(nextReviewAt: LocalDateTime?): Long? {
        if (nextReviewAt == null) return null

        val now = LocalDateTime.now()
        if (nextReviewAt.isBefore(now) || nextReviewAt.isEqual(now)) return 0

        val duration = Duration.between(now, nextReviewAt)
        return duration.toMinutes() % 60
    }

    // 학습 기록 리스트와 단어 정보 리스트를 StudiedWordResponse 리스트로 매핑함
    fun toStudiedWordResponses(
        userWordLearnings: List<UserWordLearningEntity>,
        jlptWords: List<JlptWordEntity>
    ): List<StudiedWordResponse> {
        val wordMap = jlptWords.associateBy { it.id }

        return userWordLearnings.mapNotNull { record ->
            wordMap[record.wordId]?.let { word ->
                toStudiedWordResponse(record, word)
            }
        }
    }

    // StudiedWordsResponse 생성함 (nextLastId 자동 계산)
    fun toStudiedWordsResponse(
        studiedWordResponses: List<StudiedWordResponse>,
        actualRecords: List<UserWordLearningEntity>,
        size: Int,
        hasNext: Boolean
    ): StudiedWordsResponse {
        // nextLastId 생성 (다음 페이지가 있을 때만)
        val nextLastId = if (hasNext) {
            actualRecords.lastOrNull()?.id
        } else null

        return StudiedWordsResponse(
            content = studiedWordResponses,
            size = size,
            hasNext = hasNext,
            nextLastId = nextLastId
        )
    }
}