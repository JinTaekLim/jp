package com.jp.jp.util.playwright

import com.jp.jp.util.toFormattedString
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.random.Random

@Component
class PlaywrightUtil {

    private val logger = LoggerFactory.getLogger(javaClass)

    // 사용자 에이전트 목록
    private val userAgents = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2.1 Safari/605.1.15",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
    )

    // 뷰포트 사이즈 목록
    private val viewportSizes = listOf(
        Pair(1920, 1080), Pair(1366, 768), Pair(1440, 900),
        Pair(1536, 864), Pair(1280, 720), Pair(1600, 900)
    )

    // 브라우저 실행 인수
    private val browserArgs = listOf(
        "--no-sandbox",
        "--disable-blink-features=AutomationControlled",
        "--disable-web-security",
        "--allow-running-insecure-content"
    )

    // HTTP 헤더
    private val httpHeaders = mapOf(
        "Accept-Language" to "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        "Accept-Encoding" to "gzip, deflate, br",
        "DNT" to "1",
        "Connection" to "keep-alive",
        "Upgrade-Insecure-Requests" to "1"
    )

    // 사람처럼 보이는 브라우저를 생성하고 반환함
    fun createHumanLikeBrowser(playwright: Playwright, headless: Boolean): Browser {
        val randomSlowMo = Random.nextDouble(800.0, 2000.0)

        return playwright.chromium().launch(
            BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(randomSlowMo)
                .setArgs(browserArgs)
        )
    }

    // 사람처럼 보이는 브라우저 컨텍스트를 생성하고 반환함
    fun createHumanLikeContext(browser: Browser) = browser.newContext(
        Browser.NewContextOptions()
            .setUserAgent(userAgents.random())
            .setViewportSize(viewportSizes.random().first, viewportSizes.random().second)
            .setLocale("ko-KR")
            .setTimezoneId("Asia/Seoul")
            .setExtraHTTPHeaders(httpHeaders)
    )

    // 웹드라이버 탐지를 회피하는 페이지를 생성하고 반환함
    fun createAntiDetectionPage(context: Any): Page {
        val page = (context as com.microsoft.playwright.BrowserContext).newPage()

        // 자바스크립트로 webdriver 탐지 방지
        page.addInitScript("""
            Object.defineProperty(navigator, 'webdriver', {
                get: () => undefined,
            });

            window.chrome = {
                runtime: {}
            };

            Object.defineProperty(navigator, 'plugins', {
                get: () => [1, 2, 3, 4, 5],
            });

            Object.defineProperty(navigator, 'languages', {
                get: () => ['ko-KR', 'ko'],
            });
        """.trimIndent())

        return page
    }

    // 사람처럼 보이는 행동 패턴을 시뮬레이션함
    fun simulateHumanBehavior(page: Page) {
        // 초기 로딩 대기
        page.waitForTimeout(Random.nextDouble(1000.0, 2000.0))

        // 마우스를 랜덤 위치로 이동
        val randomX = Random.nextInt(200, 800)
        val randomY = Random.nextInt(200, 600)
        page.mouse().move(randomX.toDouble(), randomY.toDouble())
        page.waitForTimeout(Random.nextDouble(250.0, 750.0))

        // 페이지 스크롤
        page.evaluate("""
            window.scrollTo({
                top: Math.random() * 300,
                behavior: 'smooth'
            });
        """.trimIndent())
        page.waitForTimeout(Random.nextDouble(500.0, 1250.0))

        // 추가 마우스 움직임
        val randomX2 = Random.nextInt(300, 1000)
        val randomY2 = Random.nextInt(100, 400)
        page.mouse().move(randomX2.toDouble(), randomY2.toDouble())
        page.waitForTimeout(Random.nextDouble(400.0, 1000.0))
    }

    // 콘텐츠 로딩을 기다림
    fun waitForContentLoading(page: Page) {
        try {
            page.waitForSelector(".word_list", Page.WaitForSelectorOptions().setTimeout(15000.0))
        } catch (_: Exception) {
            try {
                page.waitForSelector("div[class*='word'], .item, .list-item", Page.WaitForSelectorOptions().setTimeout(10000.0))
            } catch (_: Exception) {
                page.waitForTimeout(Random.nextDouble(1500.0, 3000.0))
            }
        }
    }

    // 최종 액션을 수행함
    fun performFinalActions(page: Page) {
        // 페이지 하단으로 스크롤
        page.evaluate("""
            window.scrollTo({
                top: document.body.scrollHeight * 0.7,
                behavior: 'smooth'
            });
        """.trimIndent())
        page.waitForTimeout(Random.nextDouble(750.0, 1500.0))

        // 다시 위로 스크롤
        page.evaluate("""
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        """.trimIndent())
        page.waitForTimeout(Random.nextDouble(500.0, 1000.0))

        // 최종 대기
        page.waitForTimeout(Random.nextDouble(500.0, 1250.0))
    }

    // Playwright 인스턴스를 생성하고 반환함
    fun createPlaywright(): Playwright = Playwright.create()

    // 여러 URL을 한 번에 크롤링하고 HTML 리스트를 반환함 (하위 호환성을 위해 유지)
    fun crawlMultiplePages(urls: List<String>, headless: Boolean = true): List<String> {
        val playwright = createPlaywright()

        try {
            val browser = createHumanLikeBrowser(playwright, headless)
            val context = createHumanLikeContext(browser)
            val page = createAntiDetectionPage(context)

            val results = mutableListOf<String>()

            for ((index, url) in urls.withIndex()) {
                try {
                    logger.info("페이지 방문 시작 [${LocalDateTime.now().toFormattedString()}] - $url")
                    page.navigate(url)
                    simulateHumanBehavior(page)
                    waitForContentLoading(page)
                    performFinalActions(page)

                    val html = page.content()
                    results.add(html)

                    logger.info("페이지 크롤링 완료 [${LocalDateTime.now().toFormattedString()}] - $url")

                    if (index < urls.size - 1) {
                        page.waitForTimeout(Random.nextDouble(2000.0, 4000.0))
                    }

                } catch (e: Exception) {
                    logger.warn("페이지 크롤링 실패 [${LocalDateTime.now().toFormattedString()}] - $url, 오류: ${e.message}")
                    results.add("")
                }
            }

            browser.close()
            return results

        } finally {
            playwright.close()
        }
    }

    // 전체 크롤링 프로세스를 실행하고 HTML을 반환함 (내부적으로 crawlMultiplePages 활용)
    fun crawlPage(url: String, headless: Boolean = true): String {
        return crawlMultiplePages(listOf(url), headless).firstOrNull() ?: ""
    }
}