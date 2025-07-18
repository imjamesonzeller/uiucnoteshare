package com.uiucnoteshare.backend.repositories

import com.uiucnoteshare.backend.models.Note
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface NoteRepository : JpaRepository<Note, UUID> {
    fun findAllByCourseId(courseOfferingId: UUID): List<Note>

    fun findAllByCourse_BaseCourse_Id(baseCourseId: UUID): List<Note>

    fun findAllByAuthorId(authorId: UUID): List<Note>
}