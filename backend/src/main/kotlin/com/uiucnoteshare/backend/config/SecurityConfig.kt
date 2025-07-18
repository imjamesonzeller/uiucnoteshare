package com.uiucnoteshare.backend.config

import com.uiucnoteshare.backend.security.JwtAuthenticationEntryPoint
import com.uiucnoteshare.backend.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtFilter: JwtAuthenticationFilter,
) {

    @Bean
    fun filterChain(http: HttpSecurity, jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint): SecurityFilterChain {
        http
        .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/**").permitAll()
                it.anyRequest().authenticated()
            }
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint)}
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}