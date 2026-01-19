package com.jp.jp.domain.collect.controller.business

import com.jp.jp.domain.collect.dto.AllWordsCrawlingResponse
import com.jp.jp.domain.collect.dto.CrawlingResult
import com.jp.jp.domain.collect.dto.JlptWordsResponse
import org.springframework.stereotype.Component

@Component
class CollectControllerMapper {

    // CrawlingResult를 성공 응답으로 변환함
    fun toSuccessResponse(
        result: CrawlingResult,
        level: String,
        part: String
    ): AllWordsCrawlingResponse {
        return AllWordsCrawlingResponse(
            success = true,
            message = "모든 단어 크롤링 및 저장 완료",
            level = level,
            part = part,
            totalPages = result.totalPages,
            totalWords = result.totalWords,
            savedWords = result.savedWords
        )
    }

    // JlptWordsResponse 리스트를 성공 응답으로 변환함
    fun toSuccessResponse(
        results: List<JlptWordsResponse>,
        level: String,
        part: String,
        startPageNum: Int
    ): AllWordsCrawlingResponse {
        val totalWords = results.sumOf { it.words.size }
        return AllWordsCrawlingResponse(
            success = true,
            message = "모든 단어 크롤링 완료 (페이지 ${startPageNum}부터 시작, 이벤트를 통해 자동 저장됨)",
            level = level,
            part = part,
            totalPages = results.size,
            totalWords = totalWords,
            savedWords = totalWords  // 이벤트를 통해 자동 저장되므로 동일
        )
    }

    // 전체 단어 크롤링 실패 응답을 생성함
    fun toAllWordsFailureResponse(
        errorMessage: String,
        level: String,
        part: String
    ): AllWordsCrawlingResponse {
        return AllWordsCrawlingResponse(
            success = false,
            message = "크롤링 실패: $errorMessage",
            level = level,
            part = part,
            totalPages = 0,
            totalWords = 0,
            savedWords = 0
        )
    }

    // 단일 페이지 크롤링 실패 응답을 생성함
    fun toSinglePageFailureResponse(
        level: String,
        part: String,
        pageNum: String
    ): JlptWordsResponse {
        return JlptWordsResponse(
            words = emptyList(),
            totalCount = 0,
            totalWordCount = 0,
            level = level,
            part = part,
            page = pageNum
        )
    }
}