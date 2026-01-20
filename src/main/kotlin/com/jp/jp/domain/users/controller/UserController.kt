package com.jp.jp.domain.users.controller

import com.jp.jp.domain.users.dto.*
import com.jp.jp.domain.users.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    // 회원가입 API
    @PostMapping
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<Void> {
        return try {
            userService.register(request)
            ResponseEntity.ok().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    // 로그인 API
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Void> {
        return try {
            userService.login(request)
            ResponseEntity.ok().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
}