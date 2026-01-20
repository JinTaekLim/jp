package com.jp.jp.domain.userJlptLevel.dto

import com.jp.jp.domain.userJlptLevel.entity.JlptLevel
import jakarta.validation.constraints.NotEmpty

// 사용자 JLPT 레벨 저장 요청 DTO
data class SaveUserJlptLevelRequest(
    @field:NotEmpty(message = "JLPT 레벨을 하나 이상 선택해주세요")
    val targetLevels: List<JlptLevel>
)