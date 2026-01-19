package com.jp.jp.domain.collect.service

import com.jp.jp.domain.collect.dto.JlptWordsResponse
import com.jp.jp.domain.collect.manager.CrawlingManager
import com.jp.jp.domain.collect.manager.JlptWordManager
import com.jp.jp.domain.collect.service.business.CollectServiceMapper
import org.springframework.stereotype.Service

@Service
class CollectService(
    private val crawlingManager: CrawlingManager,
    private val jlptWordManager: JlptWordManager,
    private val collectServiceMapper: CollectServiceMapper
) {

    // 네이버 JLPT 페이지를 크롤링하고 파싱된 단어 데이터를 반환하며 DB에 저장함
    fun getNaverJlptWords(level: String, part: String, pageNum: String, headless: Boolean): JlptWordsResponse {
        val wordsResponse = crawlingManager.crawlAndParseNaverJlptOrThrow(level, part, pageNum, headless)

        // DTO를 Entity로 변환
        val jlptWords = collectServiceMapper.toEntities(wordsResponse.words, level, part, pageNum)

        // DB에 저장
        jlptWordManager.saveAll(jlptWords)

        return wordsResponse
    }
}