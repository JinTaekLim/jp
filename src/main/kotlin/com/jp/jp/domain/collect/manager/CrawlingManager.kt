package com.jp.jp.domain.collect.manager

import com.jp.jp.domain.collect.dto.JlptWordsResponse
import com.jp.jp.domain.collect.event.JlptWordSaveEvent
import com.jp.jp.domain.collect.manager.business.CrawlingManagerMapper
import com.jp.jp.domain.collect.manager.business.CrawlingManagerParser
import com.jp.jp.util.playwright.PlaywrightUtil
import com.jp.jp.util.toFormattedString
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CrawlingManager(
    private val playwrightUtil: PlaywrightUtil,
    private val crawlingManagerParser: CrawlingManagerParser,
    private val crawlingManagerMapper: CrawlingManagerMapper,
    private val eventPublisher: ApplicationEventPublisher
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    // 네이버 JLPT 페이지 URL을 생성함
    private fun getNaverJlptUrl(level: String, part: String, pageNum: String): String {
        return "https://ja.dict.naver.com/#/jlpt/list?level=$level&part=$part&page=$pageNum"
    }

    // 네이버 JLPT 페이지를 크롤링하여 HTML을 반환함
    fun crawlNaverJlptOrThrow(level: String, part: String, pageNum: String, headless: Boolean = true): String {
        // 네이버 JLPT 페이지 URL 생성
        val url = getNaverJlptUrl(level, part, pageNum)

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
        val response = crawlingManagerMapper.toJlptWordsResponse(parsedData, level, part, pageNum)

        // 단일 페이지 저장 이벤트 발행
        eventPublisher.publishEvent(JlptWordSaveEvent(response))
        logger.info("단일 페이지 저장 이벤트 발행 완료 - 레벨: $level, 품사: $part, 페이지: $pageNum")

        return response
    }

    // 지정한 레벨과 품사의 모든 단어를 크롤링하여 반환함 (브라우저 재사용으로 성능 향상)
    fun crawlAllNaverJlptWords(level: String, part: String, startPageNum: Int, headless: Boolean = true): List<JlptWordsResponse> {
        return runCatching {
            logger.info("🚀 JLPT 단어 크롤링 시작 - 레벨: $level, 품사: $part, 시작 페이지: $startPageNum")

            // 시작 페이지로 전체 단어 수 확인
            val firstResponse = crawlAndParseNaverJlptOrThrow(level, part, startPageNum.toString(), headless)
            val totalPages = calculateTotalPages(firstResponse.totalWordCount)

            logger.info("전체 단어 수: ${firstResponse.totalWordCount}개, 총 페이지 수: ${totalPages}페이지")

            if (startPageNum >= totalPages) {
                // 시작 페이지가 마지막 페이지거나 그보다 크면 시작 페이지 결과만 반환
                listOf(firstResponse)
            } else {
                logger.info("나머지 ${((startPageNum + 1)..totalPages).count()}개 페이지를 브라우저 재사용하여 크롤링 시작...")
                logger.info("크롤링 시작 시각: [${LocalDateTime.now().toFormattedString()}]")

                // 브라우저 재사용으로 각 페이지 크롤링하며 즉시 이벤트 발행
                val remainingResults = crawlMultiplePagesWithImmediateEvents(
                    level, part, (startPageNum + 1)..totalPages, totalPages, headless
                )

                // 첫 번째 페이지 + 나머지 페이지들 합치기
                val allResults = listOf(firstResponse) + remainingResults
                logger.info("JLPT 단어 크롤링 완료 - 성공한 페이지: ${allResults.size}/$totalPages, 총 단어 수: ${allResults.sumOf { it.words.size }}개")
                allResults
            }
        }.getOrElse { emptyList() }
    }

    // 브라우저 재사용으로 여러 페이지를 크롤링하면서 각 페이지마다 즉시 이벤트 발행
    private fun crawlMultiplePagesWithImmediateEvents(
        level: String,
        part: String,
        pageRange: IntRange,
        totalPages: Int,
        headless: Boolean
    ): List<JlptWordsResponse> {
        val playwright = playwrightUtil.createPlaywright()
        val results = mutableListOf<JlptWordsResponse>()

        try {
            // 브라우저 세션 생성 (한 번만)
            val browser = playwrightUtil.createHumanLikeBrowser(playwright, headless)
            val context = playwrightUtil.createHumanLikeContext(browser)
            val page = playwrightUtil.createAntiDetectionPage(context)

            // 각 페이지를 순차적으로 크롤링
            for (pageNum in pageRange) {
                try {
                    val url = getNaverJlptUrl(level, part, pageNum.toString())

                    logger.info("🔄 페이지 $pageNum/$totalPages 크롤링 시작... [$url]")

                    // 페이지 이동
                    page.navigate(url)

                    // 사람처럼 보이는 행동 패턴 시뮬레이션
                    playwrightUtil.simulateHumanBehavior(page)

                    // 콘텐츠 로딩 대기
                    playwrightUtil.waitForContentLoading(page)

                    // 최종 액션 수행
                    playwrightUtil.performFinalActions(page)

                    // HTML 획득
                    val html = page.content()

                    if (html.isNotEmpty()) {
                        // 즉시 파싱하고 이벤트 발행
                        val parsedData = crawlingManagerParser.parseJlptHtml(html)
                        val response = crawlingManagerMapper.toJlptWordsResponse(parsedData, level, part, pageNum.toString())

                        // 즉시 저장 이벤트 발행
                        eventPublisher.publishEvent(JlptWordSaveEvent(response))
                        logger.info("✅ 페이지 $pageNum/$totalPages 크롤링 및 저장 이벤트 발행 완료")

                        results.add(response)
                    } else {
                        logger.warn("⚠️ 페이지 $pageNum/$totalPages 크롤링 실패 (빈 HTML)")
                    }

                    // 페이지 간 간격 (탐지 방지)
                    if (pageNum < pageRange.last) {
                        page.waitForTimeout((2000..4000).random().toDouble())
                    }

                } catch (e: Exception) {
                    logger.error("💥 페이지 $pageNum/$totalPages 크롤링 실패: ${e.message}")
                }
            }

            // 브라우저 정리
            browser.close()

        } finally {
            playwright.close()
        }

        return results
    }

    // 전체 단어 수로부터 총 페이지 수를 계산함 (한 페이지당 10개)
    private fun calculateTotalPages(totalWordCount: Int): Int =
        if (totalWordCount <= 0) 0 else (totalWordCount + 9) / 10
}