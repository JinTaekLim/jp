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

    // 회독에 최적화된 JLPT 단어를 반환함 (복습 긴급 → 어려운 PENDING → 일반 PENDING → 새 단어 순)
    @GetMapping("/personalized")
    fun getPersonalizedWords(
        @RequestParam level: JlptLevel,
        @RequestParam(defaultValue = "10") count: Int
    ): ApiResponse<List<JlptWordEntity>> {
        val userId = authService.getCurrentUserId() // 토큰에서 userId 추출
        val personalizedWords = jlptWordService.getPersonalizedWords(userId, level, count)
        return ApiResponse.success(personalizedWords, "회독 최적화 JLPT 단어 조회가 완료되었습니다")
    }

    // 목표 레벨에 따라 하위 레벨들에서 균등 분배하여 JLPT 단어를 반환함 (N2 목표면 N2~N5에서 균등하게)
    @GetMapping("/balanced")
    fun getBalancedWords(
        @RequestParam targetLevel: JlptLevel,
        @RequestParam(defaultValue = "12") count: Int
    ): ApiResponse<List<JlptWordEntity>> {
        val balancedWords = jlptWordService.getBalancedWordsByTargetLevel(targetLevel, count)
        return ApiResponse.success(balancedWords, "목표 레벨별 균등 분배 JLPT 단어 조회가 완료되었습니다")
    }
}