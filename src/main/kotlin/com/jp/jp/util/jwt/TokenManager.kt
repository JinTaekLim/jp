package com.jp.jp.util.jwt

import com.jp.jp.domain.users.entity.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class TokenManager(
    @Value("\${jwt.secret:mySecretKey12345678901234567890123456789012345}")
    private val secretKey: String,
    @Value("\${jwt.expiration:1800000}") // 30분
    private val expirationTime: Long,
    @Value("\${jwt.refresh-expiration:604800000}") // 7일
    private val refreshExpirationTime: Long,
    private val tokenValidator: TokenValidator,
    private val tokenCreator: TokenCreator,
    private val tokenExtractor: TokenExtractor
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun getAccessToken(id: Long, userType: UserRole): String {
        return tokenCreator.createToken(id, userType, expirationTime, key)
    }

    fun getRefreshToken(id: Long, userType: UserRole): String {
        return tokenCreator.createToken(id, userType, refreshExpirationTime, key)
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    fun extractUserIdFromToken(token: String): Long {
        val claims = getClaims(token)
        return tokenExtractor.extractUserId(claims)
    }

    /**
     * JWT 토큰에서 사용자 타입 추출
     */
    fun extractUserTypeFromToken(token: String): UserRole {
        val claims = getClaims(token)
        return tokenExtractor.extractUserType(claims)
    }

    fun validateToken(token: String) {
        tokenValidator.extractClaims(token, secretKey)
    }

    private fun getClaims(token: String): Claims {
        return tokenValidator.extractClaims(token, secretKey)
    }
}