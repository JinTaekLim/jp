package com.jp.jp.domain.userWordLearning.dto

import java.time.LocalDateTime

// 학습한 단어 정보 응답 DTO
data class StudiedWordResponse(
    val wordId: Long,                           // 단어 ID
    val japanese: String,                       // 일본어 (히라가나/가타카나)
    val kanji: String?,                         // 한자 표기
    val meanings: List<String>,                 // 의미 목록
    val partOfSpeech: String,                   // 품사
    val level: String,                          // JLPT 레벨
    val totalAttempts: Int,                     // 총 시도 횟수
    val nextReviewAt: LocalDateTime?,           // 다음 복습 예정 시간
    val remainingDays: Long?,                   // 다음 복습까지 남은 일 수
    val remainingHours: Long?,                  // 다음 복습까지 남은 시간 수
    val remainingMinutes: Long?                 // 다음 복습까지 남은 분 수
)

// 학습한 단어 목록 응답 DTO (lastId 기반 무한스크롤)
data class StudiedWordsResponse(
    val content: List<StudiedWordResponse>,     // 단어 목록
    val size: Int,                              // 요청한 페이지 크기
    val hasNext: Boolean,                       // 다음 페이지 존재 여부
    val nextLastId: Long?                       // 다음 페이지를 위한 lastId (hasNext가 true일 때만)
)