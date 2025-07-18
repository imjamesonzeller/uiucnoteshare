package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.dtos.EnrolledCourseOfferingDTO
import com.uiucnoteshare.backend.repositories.EnrollmentRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class EnrollmentService(private val enrollmentRepository: EnrollmentRepository) {
    fun getEnrollmentsForUser(userId: UUID): List<EnrolledCourseOfferingDTO> {
        val enrollments = enrollmentRepository.findAllByPersonId(userId)
        return enrollments.map { enrollment ->
            val offering = enrollment.course
            val course = offering.baseCourse
            EnrolledCourseOfferingDTO(offering, course)
        }
    }
}