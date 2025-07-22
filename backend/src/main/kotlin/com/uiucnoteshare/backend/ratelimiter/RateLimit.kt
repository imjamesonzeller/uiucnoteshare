package com.uiucnoteshare.backend.ratelimiter

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RateLimit(val key: String)