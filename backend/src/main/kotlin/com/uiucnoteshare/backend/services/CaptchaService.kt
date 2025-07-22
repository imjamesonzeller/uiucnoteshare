package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.dtos.TurnstileResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@Service
class CaptchaService(
    @Value("\${turnstile.secret-key}")
    private val secretKey: String,
    private val restTemplate: RestTemplate
) {
    fun verifyToken(token: String, remoteIp: String?): Boolean {
        val url = "https://challenges.cloudflare.com/turnstile/v0/siteverify"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val body = "secret=$secretKey&response=$token" +
                (if (!remoteIp.isNullOrBlank()) "&remoteip=$remoteIp" else "")

        val entity = HttpEntity(body, headers)

        val response = restTemplate.postForEntity(url, entity, TurnstileResponse::class.java)
        val turnstile = response.body ?: return false

        return turnstile.success
    }
}