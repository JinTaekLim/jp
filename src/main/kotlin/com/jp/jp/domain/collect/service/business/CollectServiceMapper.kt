package com.jp.jp.domain.collect.service.business

import com.jp.jp.domain.collect.dto.JlptWordDto
import com.jp.jp.domain.collect.entity.JlptWord
import org.springframework.stereotype.Component

@Component
class CollectServiceMapper {

    // JlptWordDto를 JlptWord Entity로 변환함
    fun toEntity(dto: JlptWordDto, level: String, part: String, pageNum: String): JlptWord {
        return JlptWord(
            japanese = dto.japanese,
            kanji = dto.kanji,
            partOfSpeech = dto.partOfSpeech,
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
}