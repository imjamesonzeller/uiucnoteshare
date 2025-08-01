package com.uiucnoteshare.backend.dtos

import java.util.*

data class PopularCourseDTO(
    val baseCourseId: UUID,
    val courseCode: String,
    val courseName: String,
    val noteCount: Long,
)