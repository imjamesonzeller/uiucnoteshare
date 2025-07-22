package com.uiucnoteshare.backend.controllers

import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.ratelimiter.RateLimit
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {

    @GetMapping("/me")
    @RateLimit("auth-oauth")
    fun getAuthInfo(authentication: Authentication): ResponseEntity<Map<String, Any>> {
        val person = authentication.principal as Person

        val response = mapOf(
            "id" to person.id!!,
            "email" to person.email,
            "isAuthenticated" to true
        )

        return ResponseEntity.ok(response)
    }
}