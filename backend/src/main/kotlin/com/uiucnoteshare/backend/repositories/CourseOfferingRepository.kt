package com.uiucnoteshare.backend.repositories

import com.uiucnoteshare.backend.models.CourseOffering
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CourseOfferingRepository : JpaRepository<CourseOffering, UUID> {
    fun findAllByBaseCourseId(baseCourseId: UUID): List<CourseOffering>

    fun findAllBySemester(semester: String): List<CourseOffering>

    fun findByBaseCourse_DepartmentAndBaseCourse_CourseNumberAndSemester(
        department: String,
        courseNumber: String,
        semester: String
    ): CourseOffering?

    fun findAllByBaseCourseIdAndSemester(baseCourseId: UUID, semester: String): List<CourseOffering>
}