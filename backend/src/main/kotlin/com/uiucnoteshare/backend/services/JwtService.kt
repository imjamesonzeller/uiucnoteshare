package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.models.Person
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val jwtSecretBase64: String,

    @Value("\${jwt.expiration-ms}")
    private val jwtExpirationMs: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(jwtSecretBase64),
    )

    fun createToken(person: Person): String {
        val now = Date()
        val expiry = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .setSubject(person.id.toString())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .claim("email", person.email)
            .claim("firstName", person.firstName)
            .claim("lastName", person.lastName)
            .claim("isAdmin", person.isAdmin)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractSubject(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJwt(token)
            .body
            .subject
    }

    fun isTokenValid(token: String, person: Person): Boolean {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject == person.id.toString() && claims.expiration.after(Date())
    }
}