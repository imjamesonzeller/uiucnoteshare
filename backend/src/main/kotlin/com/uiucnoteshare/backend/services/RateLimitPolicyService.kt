package com.uiucnoteshare.backend.services

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.BucketConfiguration
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RateLimitPolicyService {
    fun getRateLimitConfig(key: String): BucketConfiguration {
        val limit = when (key) {
            "notes-post-delete" -> Bandwidth.simple(5, Duration.ofMinutes(1))
            "notes-get" -> Bandwidth.simple(30, Duration.ofMinutes(1))
            "auth-oauth" -> Bandwidth.simple(3, Duration.ofMinutes(1))
            "courses-get" -> Bandwidth.simple(20, Duration.ofMinutes(1))
            else -> Bandwidth.simple(10, Duration.ofMinutes(1)) // <-- default fallback
        }

        return BucketConfiguration.builder().addLimit(limit).build()
    }
}