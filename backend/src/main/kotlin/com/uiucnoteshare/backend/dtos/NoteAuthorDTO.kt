package com.uiucnoteshare.backend.dtos

import java.util.*

data class NoteAuthorDTO(
    val id: UUID?,
    val firstName: String,
    val lastName: String,
    val profilePicture: String?
)
