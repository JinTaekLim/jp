package com.jp.jp.util.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class TokenValidator(
) {

    fun extractClaims(token: String, secretKey: String): Claims {
        val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: SignatureException) {
            throw IllegalArgumentException("토큰 서명이 올바르지 않습니다")
        } catch (e: MalformedJwtException) {
            throw IllegalArgumentException("토큰 형식이 올바르지 않습니다")
        } catch (e: ExpiredJwtException) {
            throw IllegalArgumentException("토큰이 만료되었습니다")
        } catch (e: Exception) {
            throw IllegalArgumentException("토큰 검증에 실패했습니다")
        }
    }
}