package com.jp.jp.domain.users.controller

import com.jp.jp.domain.users.dto.*
import com.jp.jp.domain.users.service.UserService
import com.jp.jp.util.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    // 회원가입 API
    @PostMapping
    fun register(@Valid @RequestBody request: RegisterRequest): ApiResponse<Void> {
        userService.register(request)
        return ApiResponse.success(message = "회원가입이 완료되었습니다")
    }

    // 로그인 API
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ApiResponse<Void> {
        userService.login(request)
        return ApiResponse.success(message = "로그인이 완료되었습니다")
    }
}