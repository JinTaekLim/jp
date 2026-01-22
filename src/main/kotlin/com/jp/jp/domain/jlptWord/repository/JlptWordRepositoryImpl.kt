package com.jp.jp.domain.jlptWord.repository

import com.jp.jp.domain.jlptWord.entity.QJlptWordEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class JlptWordRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : JlptWordRepositoryCustom {

    private val word = QJlptWordEntity.jlptWordEntity

    // ID만 조회하는 초고속 쿼리 - 캐싱용
    override fun findIdsByLevel(level: String): List<Long> {
        return queryFactory
            .select(word.id)
            .from(word)
            .where(word.level.eq(level))
            .fetch()
    }
}