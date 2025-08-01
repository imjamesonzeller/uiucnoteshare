package com.uiucnoteshare.backend.repositories

import com.uiucnoteshare.backend.dtos.PopularCourseDTO
import com.uiucnoteshare.backend.models.Note
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface NoteRepository : JpaRepository<Note, UUID> {
    fun findAllByCourseId(courseOfferingId: UUID): List<Note>

    fun findAllByCourse_BaseCourse_Id(baseCourseId: UUID): List<Note>

    fun findAllByAuthorId(authorId: UUID): List<Note>

    fun findAllByCourseIdIn(offeringIds: List<UUID>): List<Note>

    @Query("""
    SELECT new com.uiucnoteshare.backend.dtos.PopularCourseDTO(
        bc.id,
        MIN(co.classCode),
        bc.name,
        COUNT(n.id)
    )
    FROM Note n
    JOIN CourseOffering co ON n.course = co
    JOIN BaseCourse bc ON co.baseCourse = bc
    GROUP BY bc.id, bc.name
    ORDER BY COUNT(n.id) DESC
""")
    fun findTopPopularCourses(pageable: Pageable = PageRequest.of(0, 4)): List<PopularCourseDTO>
}