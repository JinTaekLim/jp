package com.jp.jp.domain.collect.manager

import com.jp.jp.domain.collect.dto.JlptWordsResponse
import com.jp.jp.domain.collect.manager.business.CrawlingManagerMapper
import com.jp.jp.domain.collect.manager.business.CrawlingManagerParser
import com.jp.jp.util.playwright.PlaywrightUtil
import org.springframework.stereotype.Component

@Component
class CrawlingManager(
    private val playwrightUtil: PlaywrightUtil,
    private val crawlingManagerParser: CrawlingManagerParser,
    private val crawlingManagerMapper: CrawlingManagerMapper
) {

    // 네이버 JLPT 페이지를 크롤링하여 HTML을 반환함
    fun crawlNaverJlptOrThrow(level: String, part: String, pageNum: String, headless: Boolean = true): String {
        // 네이버 JLPT 페이지 URL 생성
        val url = "https://ja.dict.naver.com/#/jlpt/list?level=$level&part=$part&page=$pageNum"

        // PlaywrightUtil을 사용하여 크롤링 실행
        return playwrightUtil.crawlPage(url, headless)
    }

    // 네이버 JLPT 페이지를 크롤링하여 파싱된 단어 목록을 반환함
    fun crawlAndParseNaverJlptOrThrow(level: String, part: String, pageNum: String, headless: Boolean = true): JlptWordsResponse {
        // HTML 크롤링
        val html = crawlNaverJlptOrThrow(level, part, pageNum, headless)

        // HTML 파싱하여 ParsedJlptData 생성
        val parsedData = crawlingManagerParser.parseJlptHtml(html)

        // ParsedJlptData를 JlptWordsResponse로 변환
        return crawlingManagerMapper.toJlptWordsResponse(parsedData, level, part, pageNum)
    }
}