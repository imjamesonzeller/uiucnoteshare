package com.uiucnoteshare.backend.repositories

import com.uiucnoteshare.backend.models.BaseCourse
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface BaseCourseRepository : JpaRepository<BaseCourse, UUID> {
    fun findByDepartmentAndCourseNumber(department: String, courseNumber: String): BaseCourse?

    @Query("""
    SELECT b FROM BaseCourse b
    WHERE LOWER(CONCAT(b.department, ' ', b.courseNumber)) LIKE %:query%
       OR LOWER(b.department) LIKE %:query%
       OR LOWER(b.courseNumber) LIKE %:query%
       OR LOWER(b.name) LIKE %:query%
    ORDER BY 
        CASE 
            WHEN LOWER(CONCAT(b.department, ' ', b.courseNumber)) = :query THEN 0
            WHEN LOWER(CONCAT(b.department, ' ', b.courseNumber)) LIKE :query% THEN 1
            WHEN LOWER(b.name) LIKE :query% THEN 2
            ELSE 3
        END
""")
    fun searchCourses(@Param("query") query: String, pageable: Pageable): List<BaseCourse>
}