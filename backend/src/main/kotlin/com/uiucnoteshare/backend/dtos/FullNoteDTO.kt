package com.uiucnoteshare.backend.dtos

import com.uiucnoteshare.backend.models.NoteUploadStatus
import java.time.LocalDateTime
import java.util.*

data class FullNoteDTO(
    val id: UUID?,
    val title: String,
    val caption: String?,
    val fileUrl: String,
    val createdAt: LocalDateTime?,
    val author: NoteAuthorDTO,
    val courseOfferingId: UUID?,
    val semester: String,
    val classCode: String,
    val uploadStatus: NoteUploadStatus
)