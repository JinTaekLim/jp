package com.jp.jp.domain.userWordLearning.controller

import com.jp.jp.domain.userWordLearning.dto.StudiedWordsResponse
import com.jp.jp.domain.userWordLearning.dto.WordStudyRequest
import com.jp.jp.domain.userWordLearning.service.UserWordLearningService
import com.jp.jp.domain.users.service.AuthService
import com.jp.jp.util.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/word-learning")
class UserWordLearningController(
    private val userWordLearningService: UserWordLearningService,
    private val authService: AuthService
) {

    // 단어 학습 성공 API
    @PostMapping("/success")
    fun recordWordStudySuccess(@Valid @RequestBody request: WordStudyRequest): ApiResponse<Void> {
        val userId = authService.getCurrentUserId()
        userWordLearningService.recordWordStudySuccess(userId, request.wordId)
        return ApiResponse.success(message = "단어 학습 성공이 기록되었습니다")
    }

    // 단어 학습 실패 API
    @PostMapping("/failure")
    fun recordWordStudyFailure(@Valid @RequestBody request: WordStudyRequest): ApiResponse<Void> {
        val userId = authService.getCurrentUserId()
        userWordLearningService.recordWordStudyFailure(userId, request.wordId)
        return ApiResponse.success(message = "단어 학습 실패가 기록되었습니다")
    }

    // 학습한 단어 목록 조회 API (무한스크롤 방식)
    @GetMapping("/studied-words")
    fun getStudiedWords(
        @RequestParam(value = "lastId", required = false) lastId: Long?,
        @RequestParam(value = "size", defaultValue = "20") size: Int
    ): ApiResponse<StudiedWordsResponse> {
        val userId = authService.getCurrentUserId()
        val response = userWordLearningService.getStudiedWords(userId, lastId, size)
        return ApiResponse.success(response, "학습한 단어 목록을 조회했습니다")
    }
}