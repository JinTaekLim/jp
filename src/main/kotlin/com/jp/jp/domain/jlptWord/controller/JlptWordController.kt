package com.jp.jp.domain.jlptWord.controller

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import com.jp.jp.domain.jlptWord.service.JlptWordService
import com.jp.jp.domain.users.service.AuthService
import com.jp.jp.domain.userJlptLevel.entity.JlptLevel
import com.jp.jp.util.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/jlpt-words")
class JlptWordController(
    private val jlptWordService: JlptWordService,
    private val authService: AuthService
) {

    // 순수 랜덤 JLPT 단어를 반환함 (학습 기록 고려하지 않음)
    @GetMapping("/random")
    fun getRandomWords(
        @RequestParam level: JlptLevel,
        @RequestParam(defaultValue = "10") count: Int
    ): ApiResponse<List<JlptWordEntity>> {
        val randomWords = jlptWordService.getRandomWords(level, count)
        return ApiResponse.success(randomWords, "랜덤 JLPT 단어 조회가 완료되었습니다")
    }

    // 사용자의 학습 상태를 고려한 맞춤형 JLPT 단어를 반환함 (PENDING 단어 + 새로운 랜덤 단어)
    @GetMapping("/personalized")
    fun getPersonalizedWords(
        @RequestParam level: JlptLevel,
        @RequestParam(defaultValue = "10") count: Int
    ): ApiResponse<List<JlptWordEntity>> {
        val userId = authService.getCurrentUserId() // 토큰에서 userId 추출
        val personalizedWords = jlptWordService.getPersonalizedWords(userId, level, count)
        return ApiResponse.success(personalizedWords, "사용자 맞춤 JLPT 단어 조회가 완료되었습니다")
    }
}