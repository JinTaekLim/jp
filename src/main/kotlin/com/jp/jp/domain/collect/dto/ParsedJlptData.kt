package com.jp.jp.domain.collect.dto

// HTML 파싱 결과를 담는 DTO
data class ParsedJlptWord(
    val japanese: String,      // 일본어 (히라가나/가타카나)
    val kanji: String?,        // 한자 표기 (대괄호 제거된 상태, 없으면 null)
    val partOfSpeech: String?, // 품사 (동사, 형용사, 명사 등, 없으면 null)
    val meanings: List<String> // 의미 목록 (1.만나다, 2.대면하다 등)
)

// 파싱된 JLPT 데이터 목록
data class ParsedJlptData(
    val words: List<ParsedJlptWord>,
    val totalWordCount: Int = 0  // 해당 등급의 전체 단어 수 (예: 744건)
)