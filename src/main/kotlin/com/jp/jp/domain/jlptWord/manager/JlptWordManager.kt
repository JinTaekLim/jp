package com.jp.jp.domain.jlptWord.manager

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import com.jp.jp.domain.jlptWord.repository.JlptWordRepository
import org.springframework.stereotype.Component

@Component
class JlptWordManager(
    private val jlptWordRepository: JlptWordRepository
) {

    // 단어 목록을 모두 저장함
    fun saveAll(jlptWordEntities: List<JlptWordEntity>): List<JlptWordEntity> {
        return jlptWordRepository.saveAll(jlptWordEntities)
    }
}