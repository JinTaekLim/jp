package com.jp.jp.domain.collect.controller

import com.jp.jp.domain.collect.controller.business.CollectControllerMapper
import com.jp.jp.domain.collect.dto.AllWordsCrawlingResponse
import com.jp.jp.domain.collect.dto.JlptWordsResponse
import com.jp.jp.domain.collect.service.CollectService
import com.jp.jp.util.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/collect")
class CollectController(
    private val collectService: CollectService,
    private val collectControllerMapper: CollectControllerMapper
) {

    // 네이버 일본어 사전 JLPT 페이지를 파싱하여 단어 데이터 반환
    @GetMapping("/naver-jlpt/words")
    fun crawlNaverJlptWords(
        @RequestParam(defaultValue = "5") level: String,
        @RequestParam(defaultValue = "allClass") part: String,
        @RequestParam(defaultValue = "1") pageNum: String,
        @RequestParam(defaultValue = "true") headless: Boolean
    ): ApiResponse<JlptWordsResponse> {
        return try {
            val wordsResponse = collectService.getNaverJlptWords(level, part, pageNum, headless)
            ApiResponse.success(wordsResponse, "JLPT 단어 수집이 완료되었습니다")
        } catch (e: Exception) {
            val failureResponse = collectControllerMapper.toSinglePageFailureResponse(level, part, pageNum)
            return ApiResponse.fail<JlptWordsResponse>("CRAWLING_ERROR", "JLPT 단어 수집에 실패했습니다", failureResponse)
        }
    }

    // 네이버 JLPT 레벨의 모든 단어를 크롤링하고 DB에 저장 (이벤트를 통해 자동 저장됨)
    @PostMapping("/naver-jlpt/words/all")
    fun crawlAllNaverJlptWords(
        @RequestParam(defaultValue = "5") level: String,
        @RequestParam(defaultValue = "allClass") part: String,
        @RequestParam(defaultValue = "1") startPageNum: Int,
        @RequestParam(defaultValue = "true") headless: Boolean
    ): ApiResponse<AllWordsCrawlingResponse> {
        return try {
            val results = collectService.crawlAllNaverJlptWords(level, part, startPageNum, headless)
            val successResponse = collectControllerMapper.toSuccessResponse(results, level, part, startPageNum)
            ApiResponse.success(successResponse, "전체 JLPT 단어 수집이 완료되었습니다")
        } catch (e: Exception) {
            val failureResponse = collectControllerMapper.toAllWordsFailureResponse(e.message ?: "알 수 없는 오류", level, part)
            return ApiResponse.fail<AllWordsCrawlingResponse>("CRAWLING_ERROR", "전체 JLPT 단어 수집에 실패했습니다", failureResponse)
        }
    }
}