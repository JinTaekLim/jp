package com.jp.jp.util.jwt

import com.jp.jp.domain.users.entity.UserRole
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class TokenCreator {

    fun createToken(
        id: Long,
        userType: UserRole,
        expirationTime: Long,
        key: SecretKey
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationTime)

        return Jwts.builder()
            .subject(id.toString())
            .claim("userId", id)
            .claim("role", userType.name)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }
}