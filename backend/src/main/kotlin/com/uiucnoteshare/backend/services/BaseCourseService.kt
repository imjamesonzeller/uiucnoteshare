package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.dtos.BaseCourseDetailDTO
import com.uiucnoteshare.backend.dtos.BaseCourseSummaryDTO
import com.uiucnoteshare.backend.dtos.CourseOfferingDTO
import com.uiucnoteshare.backend.repositories.BaseCourseRepository
import com.uiucnoteshare.backend.repositories.CourseOfferingRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BaseCourseService(
    private val baseCourseRepository: BaseCourseRepository,
    private val courseOfferingRepository: CourseOfferingRepository,
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
}