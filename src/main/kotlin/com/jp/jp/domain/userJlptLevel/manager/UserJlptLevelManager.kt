package com.jp.jp.domain.userJlptLevel.manager

import com.jp.jp.domain.userJlptLevel.entity.UserJlptLevelEntity
import com.jp.jp.domain.userJlptLevel.repository.UserJlptLevelRepository
import org.springframework.stereotype.Component

@Component
class UserJlptLevelManager(
    private val userJlptLevelRepository: UserJlptLevelRepository
) {

    // 여러 JLPT 레벨 엔티티를 한 번에 저장함
    fun saveAll(entities: List<UserJlptLevelEntity>): List<UserJlptLevelEntity> {
        return userJlptLevelRepository.saveAll(entities)
    }
}