package com.uiucnoteshare.backend.security

import com.uiucnoteshare.backend.repositories.PersonRepository
import com.uiucnoteshare.backend.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val personRepository: PersonRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        if (path.startsWith("/auth/oauth/")) {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.removePrefix("Bearer ").trim()

        try {
            val personId = jwtService.extractSubject(jwt)
            if (SecurityContextHolder.getContext().authentication == null) {
                val person = personRepository.findById(UUID.fromString(personId)).orElse(null)

                if (person != null && jwtService.isTokenValid(jwt, person)) {
                    val authentication = UsernamePasswordAuthenticationToken(
                        person, null, emptyList()
                    )
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (ex: Exception) {
            logger.warn("JWT processing failed: ${ex.message}")
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token")
            return
        }

        filterChain.doFilter(request, response)
    }
}