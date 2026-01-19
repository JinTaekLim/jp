package com.jp.jp.domain.collect.event

import com.jp.jp.domain.collect.manager.JlptWordManager
import com.jp.jp.domain.collect.service.business.CollectServiceMapper
import com.jp.jp.util.toFormattedString
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class JlptWordSaveEventListener(
    private val jlptWordManager: JlptWordManager,
    private val collectServiceMapper: CollectServiceMapper
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    // JLPT 단어 저장 이벤트를 처리함 (각 페이지마다 짧은 트랜잭션으로 저장)
    @EventListener
    @Transactional
    fun handleJlptWordSaveEvent(event: JlptWordSaveEvent) {
        val response = event.response

        try {
            logger.info("페이지 ${response.page} DB 저장 시작 [${LocalDateTime.now().toFormattedString()}]")

            // DTO를 Entity로 변환
            val entities = collectServiceMapper.toEntities(response.words, response.level, response.part, response.page)

            // DB에 저장 (짧은 트랜잭션)
            jlptWordManager.saveAll(entities)

            logger.info("페이지 ${response.page} DB 저장 완료 [${LocalDateTime.now().toFormattedString()}] - ${entities.size}개 단어")
        } catch (e: Exception) {
            logger.error("페이지 ${response.page} DB 저장 실패 [${LocalDateTime.now().toFormattedString()}]: ${e.message}", e)
        }
    }
}