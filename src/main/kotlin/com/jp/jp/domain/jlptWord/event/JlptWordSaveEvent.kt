package com.jp.jp.domain.jlptWord.event

import com.jp.jp.domain.collect.dto.JlptWordsResponse

// JLPT 단어 저장 이벤트 클래스 (JlptWordsResponse에 모든 정보 포함됨)
data class JlptWordSaveEvent(
    val response: JlptWordsResponse
)