package com.jp.jp.domain.jlptWord.service

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import com.jp.jp.domain.jlptWord.manager.JlptWordManager
import org.springframework.stereotype.Service

@Service
class JlptWordService(
    private val jlptWordManager: JlptWordManager
) {

    // 사용자의 요청에 따라 랜덤한 JLPT 단어를 반환함
    fun getRandomWords(level: String, count: Int): List<JlptWordEntity> {
        return jlptWordManager.getRandomWords(level, count)
    }
}