package com.jp.jp.util.exception

import com.jp.jp.util.response.ApiErrorType
import org.springframework.http.HttpStatus

/**
 * 애플리케이션 기본 예외 클래스
 */
abstract class BaseException(
    val errorType: ApiErrorType,
    val httpStatus: HttpStatus,
    message: String? = null
) : RuntimeException(message ?: errorType.message)

/**
 * 서비스 레이어에서 발생하는 예외
 */
class ServiceException(
    errorType: ApiErrorType,
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    message: String? = null
) : BaseException(errorType, httpStatus, message)

/**
 * 접근 권한 관련 예외
 */
class ForbiddenException(
    val code: String = "FORBIDDEN",
    message: String? = null
) : RuntimeException(message ?: "접근 권한이 없습니다")