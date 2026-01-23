package com.jp.jp.domain.jlptWord.manager

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import com.jp.jp.domain.jlptWord.repository.JlptWordCacheRepository
import com.jp.jp.domain.jlptWord.repository.JlptWordRepository
import com.jp.jp.domain.userJlptLevel.entity.JlptLevel
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

    // 캐시된 단어 목록에서 무작위로 N개를 선택하여 반환함
    fun getRandomWords(level: JlptLevel, count: Int): List<JlptWordEntity> {
        val dbLevel = JlptLevel.toNumber(level)

        val allIds = refreshCacheIfNeeded(dbLevel)
        // 랜덤하게 선택
        val randomIds = allIds.shuffled().take(count)

        // IN절을 통해 Entity로 변환
        return jlptWordRepository.findAllById(randomIds)
    }

    // ID 목록으로 특정 단어들을 조회함
    fun getWordsByIds(ids: List<Long>): List<JlptWordEntity> {
        return jlptWordRepository.findAllById(ids)
    }

    // 특정 ID들을 제외하고 캐시된 단어 목록에서 무작위로 N개를 선택하여 반환함
    fun getRandomWordsExcluding(level: JlptLevel, count: Int, excludeIds: Set<Long>): List<JlptWordEntity> {
        val dbLevel = JlptLevel.toNumber(level)

        // 캐시에서 모든 ID를 가져와서 excludeIds 제외하고 랜덤 선택
        val allIds = refreshCacheIfNeeded(dbLevel)
        val filteredIds = allIds.filter { it !in excludeIds }
        val randomIds = filteredIds.shuffled().take(count)

        // IN절을 통해 Entity로 변환
        return jlptWordRepository.findAllById(randomIds)
    }

    // ID 목록과 레벨로 특정 단어들을 조회함 (한 번의 쿼리로 처리)
    fun getWordsByIdsAndLevel(ids: Set<Long>, level: JlptLevel): List<JlptWordEntity> {
        val dbLevel = JlptLevel.toNumber(level)
        return ids.takeIf { it.isNotEmpty() }
            ?.let { jlptWordRepository.findByIdInAndLevel(it, dbLevel) }
            ?: emptyList()
    }

    // 캐시를 확인하고 필요시 갱신하여 단어 ID 목록을 반환함
    fun refreshCacheIfNeeded(level: String): List<Long> {

        // 캐시에서 ID들을 가져오고, 비어있으면 DB에서 로드
        var allIds = jlptWordCacheRepository.findByLevel(level)
        if (allIds.isNullOrEmpty()) {
            allIds = jlptWordRepository.findIdsByLevel(level)
            jlptWordCacheRepository.save(level, allIds)
        }
        return allIds
    }
}