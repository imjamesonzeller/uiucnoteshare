package com.uiucnoteshare.backend.controllers

import com.uiucnoteshare.backend.dtos.CreateNoteRequest
import com.uiucnoteshare.backend.dtos.CreatedNoteResponse
import com.uiucnoteshare.backend.dtos.FullNoteDTO
import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.services.NoteService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/notes")
class NoteController(
    private val noteService: NoteService
) {
    @GetMapping("/{noteId}")
    fun getNote(
        @PathVariable noteId: UUID
    ): ResponseEntity<FullNoteDTO?> {
        val note = noteService.getNote(noteId)
        return ResponseEntity.ok(note)
    }

    @PostMapping
    fun createNote(
        @Valid @RequestBody request: CreateNoteRequest,
        authentication: Authentication
    ): ResponseEntity<CreatedNoteResponse> {
        val person = authentication.principal as Person
        val result = noteService.createNote(request, person)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{noteId}")
    fun deleteNote(
        @PathVariable noteId: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val person = authentication.principal as Person
        noteService.deleteNote(noteId, person)
        return ResponseEntity.noContent().build()
    }
}