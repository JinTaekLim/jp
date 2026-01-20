package com.jp.jp.domain.jlptWord.repository

import com.jp.jp.domain.jlptWord.entity.JlptWordEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JlptWordRepository : JpaRepository<JlptWordEntity, Long> {

}