package com.uiucnoteshare.backend.controllers

import com.uiucnoteshare.backend.dtos.CourseOfferingDTO
import com.uiucnoteshare.backend.dtos.EnrolledCourseOfferingDTO
import com.uiucnoteshare.backend.dtos.NoteDTO
import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.services.CourseOfferingService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/course-offerings")
class CourseOfferingsController(
    private val courseOfferingService: CourseOfferingService
) {

    @GetMapping("")
    fun getCourseOfferings(
        @RequestParam(required = false) semester: String?,
    ): ResponseEntity<List<CourseOfferingDTO?>> {
        val offerings = courseOfferingService.getOfferings(semester)
        return ResponseEntity.ok(offerings)
    }

    @GetMapping("/{offeringId}")
    fun getCourseOfferingById(
        @PathVariable offeringId: UUID
    ): ResponseEntity<CourseOfferingDTO?> {
        val offering = courseOfferingService.getOffering(offeringId)
        return ResponseEntity.ok(offering)
    }

    @GetMapping("/{offeringId}/notes")
    fun getNotesForOffering(
        @PathVariable offeringId: UUID,
    ): ResponseEntity<List<NoteDTO?>> {
        val notes = courseOfferingService.getNotes(offeringId)
        return ResponseEntity.ok(notes)
    }

    // === Enroll and Unenroll

    @PostMapping("/{offeringId}/enroll")
    fun enrollToOffering(
        @PathVariable offeringId: UUID,
        authentication: Authentication
    ): ResponseEntity<EnrolledCourseOfferingDTO> {
        val person = authentication.principal as Person
        val enrollment = courseOfferingService.enrollUser(person, offeringId)

        val dto = EnrolledCourseOfferingDTO.fromEnrollment(enrollment)
        return ResponseEntity.ok(dto)
    }

    @DeleteMapping("/{offeringId}/enroll")
    fun unenrollFromOffering(
        @PathVariable offeringId: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val person = authentication.principal as Person
        courseOfferingService.unenrollUser(person, offeringId)
        return ResponseEntity.noContent().build()
    }
}