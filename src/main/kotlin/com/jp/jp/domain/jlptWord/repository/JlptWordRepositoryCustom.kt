package com.jp.jp.domain.jlptWord.repository

interface JlptWordRepositoryCustom {

    // ID만 조회하는 초고속 쿼리 - 캐싱용
    fun findIdsByLevel(level: String): List<Long>
}