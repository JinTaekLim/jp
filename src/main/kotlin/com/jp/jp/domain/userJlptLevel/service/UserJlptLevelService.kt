package com.jp.jp.domain.userJlptLevel.service

import com.jp.jp.domain.userJlptLevel.dto.SaveUserJlptLevelRequest
import com.jp.jp.domain.userJlptLevel.manager.UserJlptLevelManager
import com.jp.jp.domain.userJlptLevel.service.business.UserJlptLevelServiceMapper
import org.springframework.stereotype.Service

@Service
class UserJlptLevelService(
    private val userJlptLevelManager: UserJlptLevelManager,
    private val userJlptLevelServiceMapper: UserJlptLevelServiceMapper
) {

    // 사용자의 JLPT 레벨 설정을 저장함
    fun saveUserJlptLevels(userId: Long, request: SaveUserJlptLevelRequest) {
        val entities = userJlptLevelServiceMapper.toEntities(request, userId)
        userJlptLevelManager.saveAll(entities)
    }
}