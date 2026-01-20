package com.jp.jp.domain.userWordLearning.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

// 단어 학습 결과 요청 DTO
data class WordStudyRequest(
    @field:NotNull(message = "단어 ID는 필수입니다")
    @field:Positive(message = "단어 ID는 양수여야 합니다")
    val wordId: Long
)