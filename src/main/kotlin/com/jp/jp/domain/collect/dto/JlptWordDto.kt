package com.jp.jp.domain.collect.dto

// JLPT 단어 정보를 담는 DTO
data class JlptWordDto(
    val japanese: String,     // 일본어 (히라가나/가타카나)
    val kanji: String?,       // 한자 표기 (대괄호 제거된 상태, 없으면 null)
    val partOfSpeech: String, // 품사 (동사, 형용사, 명사 등)
    val meanings: List<String> // 의미 목록 (1.만나다, 2.대면하다 등)
)

// JLPT 단어 목록 응답 DTO
data class JlptWordsResponse(
    val words: List<JlptWordDto>,
    val totalCount: Int,
    val totalWordCount: Int,  // 해당 등급의 전체 단어 수 (예: 744건)
    val level: String,        // JLPT 레벨
    val part: String,         // 품사 분류
    val page: String          // 페이지 번호
)