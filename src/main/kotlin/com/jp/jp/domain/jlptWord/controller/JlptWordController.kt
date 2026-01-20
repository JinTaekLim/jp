package com.jp.jp.domain.jlptWord.controller

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import com.jp.jp.domain.jlptWord.service.JlptWordService
import com.jp.jp.domain.users.service.AuthService
import com.jp.jp.util.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/jlpt-words")
class JlptWordController(
    private val jlptWordService: JlptWordService,
    private val authService: AuthService
) {

    // 사용자의 JLPT 레벨에 따라 랜덤한 단어를 반환함
    @GetMapping("/random")
    fun getRandomWords(
        @RequestParam level: String,
        @RequestParam(defaultValue = "10") count: Int
    ): ApiResponse<List<JlptWordEntity>> {
        val userId = authService.getCurrentUserId() // 토큰에서 userId 추출
        val randomWords = jlptWordService.getRandomWords(level, count)
        return ApiResponse.success(randomWords, "랜덤 JLPT 단어 조회가 완료되었습니다")
    }
}