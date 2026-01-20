package com.jp.jp.config.log

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

// 실제 로그 출력을 담당하는 클래스
@Component
class LogPrinter {

    private val log = LoggerFactory.getLogger(LogPrinter::class.java)

    // 요청 로그 출력
    fun printRequestLog(method: String, url: String, contentType: String?, body: String?, ip: String, userId: String, role: String) {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        log.info("Request : {} uri=[{}] content-type=[{}], body=[{}], client-ip=[{}], userId=[{}], role=[{}]", method, url, contentType, body, ip, userId, role)
    }

    // 응답 로그 출력
    fun printResponseLog(status: Int, body: String?, startTime: Long) {
        log.info("Response : {} body=[{}]", status, body)
        log.info("Request processed in {}ms", (System.currentTimeMillis() - startTime))
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }
}