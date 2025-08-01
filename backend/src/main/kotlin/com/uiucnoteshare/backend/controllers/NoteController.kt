package com.uiucnoteshare.backend.controllers

import com.uiucnoteshare.backend.dtos.*
import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.ratelimiter.RateLimit
import com.uiucnoteshare.backend.repositories.NoteRepository
import com.uiucnoteshare.backend.services.CaptchaService
import com.uiucnoteshare.backend.services.NoteService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("/notes")
class NoteController(
    private val noteService: NoteService,
    private val captchaService: CaptchaService,
    private val noteRepository: NoteRepository,
) {
    @GetMapping("/{noteId}")
    @RateLimit("notes-get")
    fun getNote(
        @PathVariable noteId: UUID
    ): ResponseEntity<FullNoteDTO?> {
        val note = noteService.getNote(noteId)
        return ResponseEntity.ok(note)
    }

    @GetMapping
    @RateLimit("notes-get")
    fun getMyNotes(
        authentication: Authentication,
    ): ResponseEntity<List<NoteDTO>> {
        val personId: UUID = (authentication.principal as Person).id
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in")
        val notes = noteRepository.findAllByAuthorId(personId).map { note -> note.toNoteDTO() }
        return ResponseEntity.ok(notes)
    }

    @PostMapping
    @RateLimit("notes-post-delete")
    fun createNote(
        @Valid @RequestBody request: CreateNoteRequest,
        authentication: Authentication,
        httpRequest: HttpServletRequest
    ): ResponseEntity<CreatedNoteResponse> {
        val captchaToken = request.captchaToken
        val remoteIp = httpRequest.remoteAddr

        if (!captchaService.verifyToken(captchaToken, remoteIp)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Turnstile CAPTCHA failed")
        }

        val person = authentication.principal as Person
        val result = noteService.createNote(request, person)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{noteId}")
    @RateLimit("notes-post-delete")
    fun deleteNote(
        @PathVariable noteId: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val person = authentication.principal as Person
        noteService.deleteNote(noteId, person)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{noteId}/confirm-upload")
    @RateLimit("notes-post-delete")
    fun confirmNoteUpload(
        @PathVariable noteId: UUID,
        authentication: Authentication
    ): ResponseEntity<String> {
        val person = authentication.principal as Person
        noteService.confirmNoteUpload(noteId, person)
        return ResponseEntity.ok("Note was confirmed as uploaded.")
    }

    @GetMapping("/popular")
    @RateLimit("notes-get")
    fun getPopularNotes(): ResponseEntity<List<PopularCourseDTO>> {
        return ResponseEntity.ok(noteService.getPopularCourses())
    }
}