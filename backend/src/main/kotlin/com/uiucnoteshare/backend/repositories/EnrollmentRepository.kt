package com.uiucnoteshare.backend.repositories

import com.uiucnoteshare.backend.models.Enrollment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EnrollmentRepository : JpaRepository<Enrollment, UUID> {
    fun findAllByPersonId(personId: UUID): List<Enrollment>

    fun findByPersonIdAndCourseId(personId: UUID, courseOfferingId: UUID): Enrollment?
}