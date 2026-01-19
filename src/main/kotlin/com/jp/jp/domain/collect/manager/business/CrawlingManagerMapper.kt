package com.jp.jp.domain.collect.manager.business

import com.jp.jp.domain.collect.dto.JlptWordDto
import com.jp.jp.domain.collect.dto.JlptWordsResponse
import com.jp.jp.domain.collect.dto.ParsedJlptData
import com.jp.jp.domain.collect.dto.ParsedJlptWord
import org.springframework.stereotype.Component

@Component
class CrawlingManagerMapper {

    // ParsedJlptWord를 JlptWordDto로 변환함
    fun toJlptWordDto(parsedWord: ParsedJlptWord): JlptWordDto {
        return JlptWordDto(
            japanese = parsedWord.japanese,
            kanji = parsedWord.kanji,
            partOfSpeech = parsedWord.partOfSpeech,
            meanings = parsedWord.meanings
        )
    }

    // ParsedJlptData를 JlptWordsResponse로 변환함
    fun toJlptWordsResponse(parsedData: ParsedJlptData, level: String, part: String, pageNum: String): JlptWordsResponse {
        val words = parsedData.words.map { toJlptWordDto(it) }

        return JlptWordsResponse(
            words = words,
            totalCount = words.size,
            totalWordCount = parsedData.totalWordCount,
            level = level,
            part = part,
            page = pageNum
        )
    }
}