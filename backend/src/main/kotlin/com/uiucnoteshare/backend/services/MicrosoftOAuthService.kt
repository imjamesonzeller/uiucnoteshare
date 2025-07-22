package com.uiucnoteshare.backend.services

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import com.nimbusds.jose.util.ResourceRetriever
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.net.URL
import java.util.*

@Service
class MicrosoftOAuthService(
    @Value("\${microsoft.client-id}")
    private val clientId: String,

    @Value("\${microsoft.tenant-id}")
    private val tenantId: String,

    private val jwtService: JwtService,
    private val userService: UserService
) {
    fun authenticateAndGenerateJwt(microsoftToken: String): String {
        val claims = validateMicrosoftToken(microsoftToken)

        val email = claims.getStringClaim("email")
            ?: claims.getStringClaim("preferred_username")
        val name = claims.getStringClaim("name")
        val oid = claims.getStringClaim("oid")

        val person = userService.findOrCreateFromMicrosoft(email, oid, name)

        return jwtService.createToken(person)
    }

    private fun validateMicrosoftToken(microsoftToken: String): JWTClaimsSet {
        try {
            val signedJWT = SignedJWT.parse(microsoftToken)

            val jwksUrl = URL("https://login.microsoftonline.com/$tenantId/discovery/v2.0/keys")
            val resourceRetriever: ResourceRetriever = DefaultResourceRetriever(2000, 2000)

            val jwkSource = JWKSourceBuilder
                .create<SecurityContext>(jwksUrl, resourceRetriever)
                .build()

            val keySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, jwkSource)

            val jwtProcessor: ConfigurableJWTProcessor<SecurityContext> = DefaultJWTProcessor()
            jwtProcessor.jwsKeySelector = keySelector

            val claims = try {
                jwtProcessor.process(signedJWT, null)
            } catch (e: Exception) {
                println("JWT processing failed: ${e.message}")
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT validation failed")
            }

            val issuer = claims.issuer
            val audience = claims.audience.firstOrNull()

            if (issuer == null || !issuer.startsWith("https://login.microsoftonline.com")) {
                throw RuntimeException("Invalid issuer: $issuer")
            }

            if (audience != clientId) {
                throw RuntimeException("Invalid audience: $audience")
            }

            val now = Date()
            if (claims.expirationTime?.before(now) == true) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired")
            }

            return claims
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired Microsoft token")
        }
    }
}