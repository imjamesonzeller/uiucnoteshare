package com.uiucnoteshare.backend.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.PrintWriter
import org.springframework.security.core.AuthenticationException

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"
        val writer: PrintWriter = response.writer
        writer.println("""{ "error": "Unauthorized: ${authException.message}""")
    }
}