package com.uiucnoteshare.backend.controllers

import com.uiucnoteshare.backend.dtos.EnrolledCourseOfferingDTO
import com.uiucnoteshare.backend.dtos.PersonDTO
import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.ratelimiter.RateLimit
import com.uiucnoteshare.backend.services.EnrollmentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val enrollmentService: EnrollmentService,
) {

    @GetMapping("/me")
    @RateLimit("users")
    fun getSelfInfo(authentication: Authentication): ResponseEntity<PersonDTO> {
        val person = authentication.principal as Person
        val dto = PersonDTO(person)
        return ResponseEntity.ok(dto)
    }

    @GetMapping("/me/enrollments")
    @RateLimit("users")
    fun getSelfEnrollments(authentication: Authentication): ResponseEntity<List<EnrolledCourseOfferingDTO>> {
        val person = authentication.principal as Person
        val enrollments = enrollmentService.getEnrollmentsForUser(person.id!!)
        return ResponseEntity.ok(enrollments)
    }
}