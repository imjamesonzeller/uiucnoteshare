package com.uiucnoteshare.backend.dtos

import java.util.UUID

data class CreatedNoteResponse(
    val noteId: UUID,
    val uploadUrl: String
)
