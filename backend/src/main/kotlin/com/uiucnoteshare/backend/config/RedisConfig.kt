package com.uiucnoteshare.backend.config

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration


@Configuration
class RedisConfig(
    @Value("\${redis.host}") private val host: String,
    @Value("\${redis.port}") private val port: Int,
//    @Value("\${redis.password}") private val password: String,
) {
    private val redisClient = RedisClient.create(
            RedisURI.builder()
            .withHost(host)
            .withPort(port)
//            .withPassword(password.toCharArray())
            .build()
    )

    @Bean
    fun proxyManager(): LettuceBasedProxyManager {
        return LettuceBasedProxyManager
            .builderFor(redisClient)
            .withExpirationStrategy(
                ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1))
            )
            .build()
    }
}