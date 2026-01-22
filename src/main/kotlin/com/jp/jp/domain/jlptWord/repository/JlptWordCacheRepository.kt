package com.jp.jp.domain.jlptWord.repository

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class JlptWordCacheRepository {

    // Entity 대신 ID만 캐시 - 메모리 사용량 90% 감소!
    private val idCache: ConcurrentHashMap<String, List<Long>> = ConcurrentHashMap()

    // ID 리스트를 캐시에 저장함
    fun save(level: String, wordIds: List<Long>) {
        idCache[level] = wordIds
    }

    // 캐시에서 레벨별 ID 목록을 조회함
    fun findByLevel(level: String): List<Long>? {
        return idCache[level]
    }

    // 캐시에서 랜덤한 ID들을 선택함
    fun findRandomIdsByLevel(level: String, count: Int): List<Long> {
        return findByLevel(level)?.shuffled()?.take(count) ?: emptyList()
    }

    // 특정 레벨 캐시를 제거함
    fun deleteByLevel(level: String) {
        idCache.remove(level)
    }

    // 전체 캐시를 제거함
    fun deleteAll() {
        idCache.clear()
    }
}