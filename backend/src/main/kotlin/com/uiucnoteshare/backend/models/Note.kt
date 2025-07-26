package com.uiucnoteshare.backend.models

import com.uiucnoteshare.backend.dtos.NoteAuthorDTO
import com.uiucnoteshare.backend.dtos.NoteDTO
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "note")
data class Note(
    @Id
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: Person,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_offering_id", nullable = true)
    var course: CourseOffering,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var caption: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var uploadStatus: NoteUploadStatus = NoteUploadStatus.PENDING_UPLOAD,

    var createdAt: LocalDateTime? = null,
) {
    fun toNoteDTO(): NoteDTO {
        val author = this.author
        val offering = this.course

        return NoteDTO(
            id = this.id,
            title = this.title,
            caption = this.caption,
            createdAt = this.createdAt,
            author = NoteAuthorDTO(
                id = author.id!!,
                firstName = author.firstName,
                lastName = author.lastName,
                profilePicture = author.profilePicture
            ),
            courseOfferingId = offering.id,
            semester = offering.semester,
            classCode = offering.classCode,
            uploadStatus = this.uploadStatus
        )
    }
}
