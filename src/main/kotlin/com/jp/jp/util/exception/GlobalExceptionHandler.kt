package com.jp.jp.util.exception

import com.jp.jp.util.response.ApiErrorType
import com.jp.jp.util.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

/**
 * 글로벌 예외 핸들러
 */
@ControllerAdvice(basePackages = ["com.jp.jp"])
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * ServiceException 처리 (서비스에서 정의한 커스텀 메시지 사용)
     */
    @ExceptionHandler(ServiceException::class)
    fun handleServiceException(ex: ServiceException): ResponseEntity<ApiResponse<Any>> {
        log.warn("ServiceException occurred: {}", ex.message, ex)
        return ResponseEntity.status(ex.httpStatus)
            .body(ApiResponse.fail(ex.errorType, ex.message))
    }

    /**
     * BaseException 처리
     */
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<ApiResponse<Any>> {
        log.warn("BaseException occurred: {}", ex.message, ex)
        return ResponseEntity.status(ex.httpStatus)
            .body(ApiResponse.fail(ex.errorType, ex.message))
    }

    /**
     * ForbiddenException 처리
     */
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(ex: ForbiddenException): ResponseEntity<ApiResponse<Any>> {
        log.warn("ForbiddenException occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.fail(ex.code, ex.message ?: "접근 권한이 없습니다."))
    }

    /**
     * Spring Security AccessDeniedException 처리
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ApiResponse<Any>> {
        log.warn("AccessDeniedException occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.fail(ApiErrorType.FORBIDDEN))
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Any>> {
        log.warn("Validation exception occurred: {}", ex.message, ex)
        val errorMessage = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(ApiErrorType.VALIDATION_ERROR, errorMessage))
    }

    /**
     * MethodArgumentTypeMismatchException 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Any>> {
        log.warn("MethodArgumentTypeMismatchException occurred: {}", ex.message, ex)
        val errorMessage = "잘못된 파라미터 타입입니다: ${ex.name}"

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(ApiErrorType.BAD_REQUEST, errorMessage))
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Any>> {
        log.warn("IllegalArgumentException occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(ApiErrorType.BAD_REQUEST, ex.message ?: "잘못된 요청입니다."))
    }

    /**
     * 일반적인 Exception 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiResponse<Any>> {
        log.error("Unexpected exception occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail(ApiErrorType.INTERNAL_SERVER_ERROR))
    }
}