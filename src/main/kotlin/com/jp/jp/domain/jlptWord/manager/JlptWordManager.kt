package com.jp.jp.domain.jlptWord.manager

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import com.jp.jp.domain.jlptWord.repository.JlptWordCacheRepository
import com.jp.jp.domain.jlptWord.repository.JlptWordRepository
import org.springframework.stereotype.Component

@Component
class JlptWordManager(
    private val jlptWordRepository: JlptWordRepository,
    private val jlptWordCacheRepository: JlptWordCacheRepository
) {

    // 단어 목록을 모두 저장함
    fun saveAll(jlptWordEntities: List<JlptWordEntity>): List<JlptWordEntity> {
        return jlptWordRepository.saveAll(jlptWordEntities)
    }

    // Cache-aside 방식으로 레벨별 단어를 조회함
    fun findByLevel(level: String): List<JlptWordEntity> {
        return jlptWordCacheRepository.findByLevel(level)
            ?: jlptWordRepository.findByLevel(level).also { words ->
                jlptWordCacheRepository.save(level, words)
            }
    }

    // 캐시된 단어 목록에서 무작위로 N개를 선택하여 반환함
    fun getRandomWords(level: String, count: Int): List<JlptWordEntity> {
        val words = findByLevel(level)
        return words.shuffled().take(count)
    }

    // ID 목록으로 특정 단어들을 조회함
    fun getWordsByIds(ids: List<Long>): List<JlptWordEntity> {
        return jlptWordRepository.findAllById(ids)
    }

    // 특정 ID들을 제외하고 캐시된 단어 목록에서 무작위로 N개를 선택하여 반환함
    fun getRandomWordsExcluding(level: String, count: Int, excludeIds: Set<Long>): List<JlptWordEntity> {
        val words = findByLevel(level)
        val filteredWords = words.filter { it.id !in excludeIds }
        return filteredWords.shuffled().take(count)
    }

    // ID 목록과 레벨로 특정 단어들을 조회함 (한 번의 쿼리로 처리)
    fun getWordsByIdsAndLevel(ids: Set<Long>, level: String): List<JlptWordEntity> {
        return ids.takeIf { it.isNotEmpty() }
            ?.let { jlptWordRepository.findByIdInAndLevel(it, level) }
            ?: emptyList()
    }
}