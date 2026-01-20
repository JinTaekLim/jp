package com.jp.jp.domain.jlptWord.repository

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JlptWordRepository : JpaRepository<JlptWordEntity, Long> {

    // 특정 레벨의 모든 단어를 조회함
    fun findByLevel(level: String): List<JlptWordEntity>
}