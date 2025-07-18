package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.config.CloudflareR2Client
import com.uiucnoteshare.backend.dtos.CreateNoteRequest
import com.uiucnoteshare.backend.dtos.CreatedNoteResponse
import com.uiucnoteshare.backend.dtos.FullNoteDTO
import com.uiucnoteshare.backend.dtos.NoteAuthorDTO
import com.uiucnoteshare.backend.models.Note
import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.repositories.CourseOfferingRepository
import com.uiucnoteshare.backend.repositories.NoteRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID
import java.time.Duration

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val cloudflareR2Client: CloudflareR2Client,
    private val courseOfferingRepository: CourseOfferingRepository
) {

    fun getNote(noteId: UUID): FullNoteDTO? {
        val note: Note = noteRepository.findByIdOrNull(noteId)
            ?: return null

        return note.toFullNoteDTO()
    }

    fun Note.toFullNoteDTO(): FullNoteDTO {
        val author = this.author
        val offering = this.course

        val objectKey = "notes/${this.id}.pdf"
        val presignedUrl = cloudflareR2Client.generatePresignedReadUrl(
            "uiuc-note-share",
            objectKey,
            Duration.ofMinutes(10)
        )

        return FullNoteDTO(
            id = this.id,
            title = this.title,
            caption = this.caption,
            createdAt = this.createdAt,
            fileUrl = presignedUrl,
            author = NoteAuthorDTO(
                id = author.id!!,
                firstName = author.firstName,
                lastName = author.lastName,
                profilePicture = author.profilePicture
            ),
            courseOfferingId = offering.id,
            semester = offering.semester,
            classCode = offering.classCode
        )
    }

    fun createNote(
        request: CreateNoteRequest,
        person: Person
        ): CreatedNoteResponse {
        val course = courseOfferingRepository.findById(request.courseOfferingId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found") }

        val note = Note(
            title = request.title,
            caption = request.caption,
            course = course,
            author = person,
        )


        val savedNote = noteRepository.save(note)
        val uploadUrl = cloudflareR2Client.generatePresignedUploadUrl(
            bucketName = "uiuc-note-share",
            objectKey = "notes/${savedNote.id}.pdf",
            expiration = Duration.ofMinutes(10)
        )

        return CreatedNoteResponse(
            noteId = savedNote.id!!,
            uploadUrl = uploadUrl
        )
    }

    fun deleteNote(noteId: UUID, person: Person) {
        val note = noteRepository.findByIdOrNull(noteId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found")

        if (note.author.id != person.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User is not allowed to delete this note")
        }

        noteRepository.delete(note)
        cloudflareR2Client.deleteObject("uiuc-note-share", "notes/${note.id}.pdf")
    }
}