package com.jp.jp.domain.userJlptLevel.controller

import com.jp.jp.domain.userJlptLevel.dto.SaveUserJlptLevelRequest
import com.jp.jp.domain.userJlptLevel.service.UserJlptLevelService
import com.jp.jp.domain.users.service.AuthService
import com.jp.jp.util.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user-jlpt-levels")
class UserJlptLevelController(
    private val userJlptLevelService: UserJlptLevelService,
    private val authService: AuthService
) {

    // 사용자의 JLPT 목표 레벨을 설정함
    @PostMapping
    fun saveUserJlptLevels(@Valid @RequestBody request: SaveUserJlptLevelRequest): ApiResponse<Void> {
        val userId = authService.getCurrentUserId()
        userJlptLevelService.saveUserJlptLevels(userId, request)
        return ApiResponse.success(message = "JLPT 레벨 설정이 완료되었습니다")
    }
}