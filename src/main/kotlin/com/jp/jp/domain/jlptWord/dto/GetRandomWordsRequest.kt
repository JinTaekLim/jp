package com.jp.jp.domain.jlptWord.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

// 랜덤 JLPT 단어 요청 DTO
data class GetRandomWordsRequest(
    @field:NotBlank(message = "JLPT 레벨은 필수입니다")
    val level: String,

    @field:Min(value = 1, message = "최소 1개 이상의 단어를 요청해야 합니다")
    @field:Max(value = 50, message = "최대 50개까지만 요청 가능합니다")
    val count: Int
)