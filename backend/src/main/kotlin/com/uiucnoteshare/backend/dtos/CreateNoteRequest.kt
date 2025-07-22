package com.uiucnoteshare.backend.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateNoteRequest(
    @field:NotBlank
    val title: String,

    val caption: String? = null,

    @field:NotNull
    val courseOfferingId: UUID,

    @field:NotNull
    val fileSizeByBytes: Long,

    @field:NotBlank
    val fileType: String,

    @field:NotNull
    val captchaToken: String
)
