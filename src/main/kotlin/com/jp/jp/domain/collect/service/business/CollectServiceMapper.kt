package com.jp.jp.domain.collect.service.business

import com.jp.jp.domain.collect.dto.CrawlingResult
import com.jp.jp.domain.collect.dto.JlptWordDto
import com.jp.jp.domain.collect.dto.JlptWordsResponse
import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import org.springframework.stereotype.Component

@Component
class CollectServiceMapper {

    // JlptWordDto를 JlptWord Entity로 변환함
    fun toEntity(dto: JlptWordDto, level: String, part: String, pageNum: String): JlptWordEntity {
        return JlptWordEntity(
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
    fun toEntities(dtos: List<JlptWordDto>, level: String, part: String, pageNum: String): List<JlptWordEntity> {
        return dtos.map { dto ->
            toEntity(dto, level, part, pageNum)
        }
    }
}