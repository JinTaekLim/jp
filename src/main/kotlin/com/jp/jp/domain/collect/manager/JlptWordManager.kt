package com.jp.jp.domain.collect.manager

import com.jp.jp.domain.collect.entity.JlptWord
import com.jp.jp.domain.collect.repository.JlptWordRepository
import org.springframework.stereotype.Component

@Component
class JlptWordManager(
    private val jlptWordRepository: JlptWordRepository
) {

    // 단어 목록을 모두 저장함
    fun saveAll(jlptWords: List<JlptWord>): List<JlptWord> {
        return jlptWordRepository.saveAll(jlptWords)
    }
}