package com.uiucnoteshare.backend.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class TurnstileResponse(
    val success: Boolean,

    @JsonProperty("challenge_ts")
    val challengeTs: String?,

    val hostname: String?,

    @JsonProperty("error-codes")
    val errorCodes: List<String>?,

    val action: String? = null,
    val cdata: String? = null,
    val metadata: Metadata? = null
)

data class Metadata(
    @JsonProperty("ephemeral_id")
    val ephemeralId: String?
)