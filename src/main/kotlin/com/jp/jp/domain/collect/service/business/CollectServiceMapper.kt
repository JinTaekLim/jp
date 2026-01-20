package com.jp.jp.domain.collect.service.business

import com.jp.jp.domain.collect.dto.CrawlingResult
import com.jp.jp.domain.collect.dto.JlptWordDto
import com.jp.jp.domain.collect.dto.JlptWordsResponse
import com.jp.jp.domain.collect.entity.JlptWord
import org.springframework.stereotype.Component

@Component
class CollectServiceMapper {

    // JlptWordDto를 JlptWord Entity로 변환함
    fun toEntity(dto: JlptWordDto, level: String, part: String, pageNum: String): JlptWord {
        return JlptWord(
            japanese = dto.japanese,
            kanji = dto.kanji,
            partOfSpeech = dto.partOfSpeech ?: "미분류", // null인 경우 기본값 제공
            meanings = dto.meanings,
            level = level,
            part = part,
            pageNum = pageNum
        )
    }

    // JlptWordDto 목록을 JlptWord Entity 목록으로 변환함
    fun toEntities(dtos: List<JlptWordDto>, level: String, part: String, pageNum: String): List<JlptWord> {
        return dtos.map { dto ->
            toEntity(dto, level, part, pageNum)
        }
    }

    // JlptWordsResponse 목록을 JlptWord Entity 목록으로 변환함 (모든 페이지 처리)
    fun toAllEntities(responses: List<JlptWordsResponse>): List<JlptWord> {
        return responses.flatMap { response ->
            response.words.map { dto ->
                toEntity(dto, response.level, response.part, response.page)
            }
        }
    }

    // 크롤링 응답과 저장된 Entity로부터 CrawlingResult를 생성함
    fun toCrawlingResult(
        responses: List<JlptWordsResponse>,
        savedEntities: List<JlptWord>
    ): CrawlingResult {
        val totalCrawledWords = responses.sumOf { it.words.size }

        return CrawlingResult(
            totalPages = responses.size,
            totalWords = totalCrawledWords,
            savedWords = savedEntities.size
        )
    }
}