package com.jp.jp.domain.collect.controller

import com.jp.jp.domain.collect.dto.JlptWordsResponse
import com.jp.jp.domain.collect.service.CollectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/collect")
class CollectController(
    private val collectService: CollectService
) {

    // 네이버 일본어 사전 JLPT 페이지를 파싱하여 단어 데이터 반환
    @GetMapping("/naver-jlpt/words")
    fun crawlNaverJlptWords(
        @RequestParam(defaultValue = "5") level: String,
        @RequestParam(defaultValue = "allClass") part: String,
        @RequestParam(defaultValue = "1") pageNum: String,
        @RequestParam(defaultValue = "true") headless: Boolean
    ): ResponseEntity<JlptWordsResponse> {

        return try {
            val wordsResponse = collectService.getNaverJlptWords(level, part, pageNum, headless)
            ResponseEntity.ok(wordsResponse)
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                JlptWordsResponse(
                    words = emptyList(),
                    totalCount = 0,
                    level = level,
                    part = part,
                    page = pageNum
                )
            )
        }
    }
}