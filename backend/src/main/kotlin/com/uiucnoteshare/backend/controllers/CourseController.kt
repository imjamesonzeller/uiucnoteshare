package com.uiucnoteshare.backend.controllers

import com.uiucnoteshare.backend.dtos.BaseCourseDetailDTO
import com.uiucnoteshare.backend.dtos.BaseCourseSummaryDTO
import com.uiucnoteshare.backend.dtos.CourseOfferingDTO
import com.uiucnoteshare.backend.dtos.NoteDTO
import com.uiucnoteshare.backend.services.BaseCourseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/courses")
class CourseController(
    val baseCourseService: BaseCourseService
) {

    @GetMapping
    fun getBaseCourses(): ResponseEntity<List<BaseCourseSummaryDTO>> {
        val courses = baseCourseService.getAllCourses()
        return ResponseEntity.ok(courses)
    }

    @GetMapping("/{courseId}")
    fun getCourse(@PathVariable courseId: UUID): ResponseEntity<BaseCourseDetailDTO?> {
        val course = baseCourseService.getCourse(courseId)
        return ResponseEntity.ok(course)
    }

    @GetMapping("/{courseId}/offerings")
    fun getCourseOfferings(
        @PathVariable courseId: UUID,
        @RequestParam(required = false) semester: String?
    ): ResponseEntity<List<CourseOfferingDTO?>> {
        val offerings = baseCourseService.getOfferingsByBaseCourse(courseId, semester)
        return ResponseEntity.ok(offerings)
    }

    @GetMapping("/{courseId}/notes")
    fun getNotesForCourse(
        @PathVariable courseId: UUID,
        @RequestParam(required = false) semester: String?
    ): ResponseEntity<List<NoteDTO>> {
        val notes = baseCourseService.getNotesForCourse(courseId, semester)
        return ResponseEntity.ok(notes)
    }
}