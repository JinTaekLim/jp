package com.jp.jp.domain.userJlptLevel.repository

import com.jp.jp.domain.userJlptLevel.entity.UserJlptLevelEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJlptLevelRepository : JpaRepository<UserJlptLevelEntity, Long>