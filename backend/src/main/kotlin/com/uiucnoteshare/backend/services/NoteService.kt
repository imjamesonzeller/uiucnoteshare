package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.config.CloudflareR2Client
import com.uiucnoteshare.backend.dtos.*
import com.uiucnoteshare.backend.models.Note
import com.uiucnoteshare.backend.models.NoteUploadStatus
import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.repositories.CourseOfferingRepository
import com.uiucnoteshare.backend.repositories.NoteRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import java.time.Duration

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val cloudflareR2Client: CloudflareR2Client,
    private val courseOfferingRepository: CourseOfferingRepository,
    private val noteProcessingService: NoteProcessingService,
    private val asyncNoteProcessor: AsyncNoteProcessor,
) {

    companion object {
        private const val NOTE_BUCKET = "uiuc-note-share"
        private const val QUARANTINE_BUCKET = "uiuc-note-share-quarantine"
    }

    fun getNote(noteId: UUID): FullNoteDTO? {
        val note: Note = noteRepository.findByIdOrNull(noteId)
            ?: return null

        return note.toFullNoteDTO()
    }

    fun Note.toFullNoteDTO(): FullNoteDTO {
        val author = this.author
        val offering = this.course

        val objectKey = "${this.id}.pdf"
        val presignedUrl = cloudflareR2Client.generatePresignedReadUrl(
            NOTE_BUCKET,
            objectKey,
            Duration.ofMinutes(2)
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
            classCode = offering.classCode,
            uploadStatus = this.uploadStatus
        )
    }

    fun createNote(
        request: CreateNoteRequest,
        person: Person
    ): CreatedNoteResponse {
        val kMaxBytes = 10 * 1024 * 1024L
        if (request.fileSizeByBytes > kMaxBytes) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File too large")
        }

        if (request.fileType != "application/pdf") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDFs are supported")
        }

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
            bucketName = NOTE_BUCKET,
            objectKey = "${savedNote.id}.pdf",
            expiration = Duration.ofMinutes(2)
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
        cloudflareR2Client.deleteObject(NOTE_BUCKET, "${note.id}.pdf")
    }

    fun confirmNoteUpload(noteId: UUID, person: Person) {
        val note = noteRepository.findByIdOrNull(noteId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found")

        if (note.author.id != person.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User is not allowed to confirm this note")
        }

        if (note.uploadStatus == NoteUploadStatus.UPLOADED) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "File already uploaded")
        }

        withDownloadedNote(NOTE_BUCKET, "${note.id}.pdf", cloudflareR2Client) { notePdfPath ->
            val (isSafe, reason) = noteProcessingService.isSafeNote(notePdfPath)
            if (!isSafe) {
                // TODO: Add logging to figure out what was "wrong" with the file.
                // TODO: Add email service to email me when this happens so I can review ASAP
                // Move PDF to quarantine for manual review
                cloudflareR2Client.updateOrCreateObject(
                    QUARANTINE_BUCKET,
                    "${reason}-${note.id}.pdf",
                    notePdfPath
                )

                cloudflareR2Client.deleteObject(NOTE_BUCKET, "${note.id}.pdf")

                note.uploadStatus = NoteUploadStatus.QUARANTINED
                noteRepository.save(note)

                throw ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Note is pending manual review due to possible policy violation: $reason"
                )
            }

            asyncNoteProcessor.processUploadedNote(note, notePdfPath)
        }
    }

    fun <T> withDownloadedNote(
        bucket: String,
        key: String,
        client: CloudflareR2Client,
        action: (Path) -> T
    ): T {
        val path = client.downloadObject(bucket, key)
        return try {
            action(path)
        } finally {
            Files.deleteIfExists(path)
        }
    }

    @Cacheable("popularCourses")
    fun getPopularCourses(): List<PopularCourseDTO> {
        return noteRepository.findTopPopularCourses()
    }
}