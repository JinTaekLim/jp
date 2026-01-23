package com.jp.jp.domain.jlptWord.service

import com.jp.jp.domain.jlptWord.manager.JlptWordManager
import com.jp.jp.domain.userJlptLevel.entity.JlptLevel
import org.springframework.stereotype.Service

@Service
class JlptWordCacheService(
    private val jlptWordManager: JlptWordManager
) {

    // 특정 레벨의 JLPT 단어 캐시를 갱신함
    fun refreshCacheByLevel(level: JlptLevel) {
        val dbLevel = JlptLevel.toNumber(level)
        jlptWordManager.refreshCacheIfNeeded(dbLevel)
    }
}