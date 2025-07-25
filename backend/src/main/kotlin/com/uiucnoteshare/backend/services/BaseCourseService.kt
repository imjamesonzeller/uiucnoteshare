package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.dtos.*
import com.uiucnoteshare.backend.models.BaseCourse
import com.uiucnoteshare.backend.models.Note
import com.uiucnoteshare.backend.repositories.BaseCourseRepository
import com.uiucnoteshare.backend.repositories.CourseOfferingRepository
import com.uiucnoteshare.backend.repositories.NoteRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class BaseCourseService(
    private val baseCourseRepository: BaseCourseRepository,
    private val courseOfferingRepository: CourseOfferingRepository,
    private val noteRepository: NoteRepository,
) {
    fun getAllCourses(): List<BaseCourseSummaryDTO> {
        val courses = baseCourseRepository.findAll()
        return courses.map { BaseCourseSummaryDTO(it) }
    }

    fun getCourse(courseId: UUID): BaseCourseDetailDTO? {
        val course = baseCourseRepository.findByIdOrNull(courseId) ?: return null
        return BaseCourseDetailDTO(course)
    }

    fun getOfferingsByBaseCourse(courseId: UUID, semester: String?): List<CourseOfferingDTO?> {
        val offerings = if (semester != null) {
            courseOfferingRepository.findAllByBaseCourseIdAndSemester(courseId, semester)
        } else {
            courseOfferingRepository.findAllByBaseCourseId(courseId)
        }

        return offerings.map { CourseOfferingDTO(it) }
    }

    fun getNotesForCourse(courseId: UUID, semester: String?): List<NoteDTO> {
        if (!baseCourseRepository.existsById(courseId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found")
        }

        val offerings = if (semester != null) {
            courseOfferingRepository.findAllByBaseCourseIdAndSemester(courseId, semester)
        } else {
            courseOfferingRepository.findAllByBaseCourseId(courseId)
        }

        if (offerings.isEmpty()) {
            return emptyList()
        }

        val offeringIds = offerings.map { it.id!! }
        val notes = noteRepository.findAllByCourseIdIn(offeringIds)
        return notes.map { it.toNoteDTO() }
    }

    fun searchCourses(query: String, limit: Int): List<BaseCourseSummaryDTO> {
        if (query.isBlank()) return emptyList()
        val sanitized = query.trim().lowercase()
        val pageable = PageRequest.of(0, limit)
        val courses: List<BaseCourse> = baseCourseRepository.searchCourses(sanitized, pageable)
        return courses.map { BaseCourseSummaryDTO(it) }
    }
}