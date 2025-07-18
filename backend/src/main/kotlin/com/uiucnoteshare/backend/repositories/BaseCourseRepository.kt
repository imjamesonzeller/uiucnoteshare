package com.uiucnoteshare.backend.repositories

import com.uiucnoteshare.backend.models.BaseCourse
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BaseCourseRepository : JpaRepository<BaseCourse, UUID> {
    fun findByDepartmentAndCourseNumber(department: String, courseNumber: String): BaseCourse?
}