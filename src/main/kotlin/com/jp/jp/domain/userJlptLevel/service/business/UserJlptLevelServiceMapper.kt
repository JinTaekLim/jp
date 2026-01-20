package com.jp.jp.domain.userJlptLevel.service.business

import com.jp.jp.domain.userJlptLevel.dto.SaveUserJlptLevelRequest
import com.jp.jp.domain.userJlptLevel.entity.JlptLevel
import com.jp.jp.domain.userJlptLevel.entity.UserJlptLevelEntity
import org.springframework.stereotype.Component

@Component
class UserJlptLevelServiceMapper {

    // 개별 UserJlptLevelEntity를 생성함
    fun toEntity(userId: Long, level: JlptLevel): UserJlptLevelEntity {
        return UserJlptLevelEntity(
            userId = userId,
            targetLevel = level
        )
    }

    // Request와 userId로 UserJlptLevelEntity 리스트를 생성함
    fun toEntities(request: SaveUserJlptLevelRequest, userId: Long): List<UserJlptLevelEntity> {
        return request.targetLevels.map { level ->
            toEntity(userId, level)
        }
    }
}