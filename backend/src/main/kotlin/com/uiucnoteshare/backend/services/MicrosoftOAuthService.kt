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
import com.nimbusds.jwt.proc.JWTProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.net.URL

@Service
class MicrosoftOAuthService(
    @Value("\${microsoft.client-id}")
    private val clientId: String,
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

            val jwksUrl = URL("https://login.microsoftonline.com/common/discovery/v2.0/keys")
            val resourceRetriever: ResourceRetriever = DefaultResourceRetriever(2000, 2000)

            val jwkSource = JWKSourceBuilder
                .create<SecurityContext>(jwksUrl, resourceRetriever)
                .build()

            val keySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, jwkSource)

            val jwtProcessor: ConfigurableJWTProcessor<SecurityContext> = DefaultJWTProcessor()
            jwtProcessor.jwsKeySelector = keySelector

            val claims = jwtProcessor.process(signedJWT, null)

            val issuer = claims.issuer
            val audience = claims.audience.firstOrNull()

            if (issuer == null || !issuer.startsWith("https://login.microsoftonline.com")) {
                throw RuntimeException("Invalid issuer: $issuer")
            }

            if (audience != clientId) {
                throw RuntimeException("Invalid audience: $audience")
            }

            return claims
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired Microsoft token")
        }
    }
}