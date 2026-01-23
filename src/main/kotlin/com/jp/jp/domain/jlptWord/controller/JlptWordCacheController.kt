package com.jp.jp.domain.jlptWord.controller

import com.jp.jp.domain.jlptWord.service.JlptWordCacheService
import com.jp.jp.domain.userJlptLevel.entity.JlptLevel
import com.jp.jp.util.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/jlpt-word-cache")
class JlptWordCacheController(
    private val jlptWordCacheService: JlptWordCacheService
) {

    // JLPT 단어 캐시 갱신 API
    @PostMapping("/refresh")
    fun refreshCache(@RequestParam level: JlptLevel): ApiResponse<Void> {
        jlptWordCacheService.refreshCacheByLevel(level)
        return ApiResponse.success(message = "${level.name} 레벨 단어 캐시가 갱신되었습니다")
    }
}