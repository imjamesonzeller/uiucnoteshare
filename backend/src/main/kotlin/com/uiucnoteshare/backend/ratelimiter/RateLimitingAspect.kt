package com.uiucnoteshare.backend.ratelimiter

import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.services.RateLimitPolicyService
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.server.ResponseStatusException

@Aspect
@Component
class RateLimitingAspect(
    private val proxyManager: LettuceBasedProxyManager,
    private val rateLimitPolicyService: RateLimitPolicyService
) {

    @Around("@annotation(rateLimit)")
    fun rateLimit(
        joinPoint: ProceedingJoinPoint,
        rateLimit: RateLimit
    ): Any {
        val auth = SecurityContextHolder.getContext().authentication
        val userId = (auth?.principal as? Person)?.id?.toString()

        val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        val ipAddress = requestAttributes?.request?.remoteAddr ?: "unknown"

        val redisKey = if (userId != null) {
            "${rateLimit.key}:$userId"
        } else {
            "${rateLimit.key}:ip:$ipAddress"
        }

        val config = rateLimitPolicyService.getRateLimitConfig(rateLimit.key)

        val bucket = proxyManager.builder().build(redisKey.toByteArray()) { config }

        return if (bucket.tryConsume(1)) {
            joinPoint.proceed()
        } else {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded")
        }
    }
}