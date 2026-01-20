package com.jp.jp.util.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

/**
 * API 통합 응답 포맷
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val code: String,
    val data: T? = null,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T? = null, message: String = "성공"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                code = "SUCCESS",
                data = data
            )
        }

        fun <T> fail(errorType: ApiErrorType, customMessage: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = customMessage ?: errorType.message,
                code = errorType.name,
                data = null
            )
        }

        fun <T> fail(code: String, message: String, data: T? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                code = code,
                data = data
            )
        }
    }
}