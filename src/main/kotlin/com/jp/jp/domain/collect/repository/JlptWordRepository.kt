package com.jp.jp.domain.collect.repository

import com.jp.jp.domain.collect.entity.JlptWord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JlptWordRepository : JpaRepository<JlptWord, Long> {

    // 일본어로 단어 검색 (부분 일치)
    fun findByJapaneseContaining(japanese: String): List<JlptWord>

    // JLPT 레벨별 단어 조회
    fun findByLevel(level: String): List<JlptWord>

    // 품사별 단어 조회
    fun findByPartOfSpeech(partOfSpeech: String): List<JlptWord>

    // 레벨과 품사로 단어 조회
    fun findByLevelAndPart(level: String, part: String): List<JlptWord>

    // 중복 체크용 - 같은 일본어 단어가 같은 레벨, 페이지에 이미 존재하는지 확인
    fun existsByJapaneseAndLevelAndPartAndPageNum(
        japanese: String,
        level: String,
        part: String,
        pageNum: String
    ): Boolean

    // 페이지별 단어 개수 조회
    @Query("SELECT COUNT(j) FROM JlptWord j WHERE j.level = :level AND j.part = :part AND j.pageNum = :pageNum")
    fun countByLevelAndPartAndPageNum(
        @Param("level") level: String,
        @Param("part") part: String,
        @Param("pageNum") pageNum: String
    ): Long

    // 전체 통계 조회 (레벨별 단어 개수)
    @Query("SELECT j.level, COUNT(j) FROM JlptWord j GROUP BY j.level")
    fun getWordCountByLevel(): List<Array<Any>>
}