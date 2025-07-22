package com.uiucnoteshare.backend.controllers

import com.uiucnoteshare.backend.ratelimiter.RateLimit
import com.uiucnoteshare.backend.services.MicrosoftOAuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth/oauth")
class OAuthController(
    private val microsoftOAuthService: MicrosoftOAuthService
) {

    @PostMapping("/microsoft")
    @RateLimit("auth-oauth")
    fun microsoftLogin(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Any> {
        val token = authHeader.removePrefix("Bearer").trim()
            .substringBefore("&")

        return try {
            val jwt = microsoftOAuthService.authenticateAndGenerateJwt(token)
            ResponseEntity.ok(mapOf("token" to jwt))
        } catch (e: ResponseStatusException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to e.reason))
        }
    }
}