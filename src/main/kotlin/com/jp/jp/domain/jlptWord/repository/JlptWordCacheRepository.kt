package com.jp.jp.domain.jlptWord.repository

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class JlptWordCacheRepository {

    private val cache: ConcurrentHashMap<String, List<JlptWordEntity>> = ConcurrentHashMap()

    // 캐시에서 레벨별 단어 목록을 조회함
    fun findByLevel(level: String): List<JlptWordEntity>? {
        return cache[level]
    }

    // 캐시에 레벨별 단어 목록을 저장함
    fun save(level: String, words: List<JlptWordEntity>) {
        cache[level] = words
    }

    // 특정 레벨 캐시를 제거함
    fun deleteByLevel(level: String) {
        cache.remove(level)
    }

    // 전체 캐시를 제거함
    fun deleteAll() {
        cache.clear()
    }
}