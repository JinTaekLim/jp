package com.jp.jp.domain.collect.service

import com.jp.jp.domain.collect.dto.JlptWordsResponse
import com.jp.jp.domain.collect.manager.CrawlingManager
import org.springframework.stereotype.Service

@Service
class CollectService(
    private val crawlingManager: CrawlingManager
) {

    // 네이버 JLPT 페이지를 크롤링하고 파싱된 단어 데이터를 반환함 (이벤트를 통해 자동 저장됨)
    fun getNaverJlptWords(level: String, part: String, pageNum: String, headless: Boolean): JlptWordsResponse {
        return crawlingManager.crawlAndParseNaverJlptOrThrow(level, part, pageNum, headless)
    }

    // 지정한 레벨과 품사의 모든 단어를 크롤링함 (이벤트를 통해 자동 저장됨)
    fun crawlAllNaverJlptWords(level: String, part: String, startPageNum: Int, headless: Boolean): List<JlptWordsResponse> {
        return crawlingManager.crawlAllNaverJlptWords(level, part, startPageNum, headless)
    }
}