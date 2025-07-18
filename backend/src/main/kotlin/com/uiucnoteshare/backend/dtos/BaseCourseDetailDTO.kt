package com.uiucnoteshare.backend.dtos

import com.uiucnoteshare.backend.models.BaseCourse
import java.util.*

data class BaseCourseDetailDTO(
    val id: UUID?,
    val department: String,
    val courseNumber: String,
    val name: String,
    val description: String?,
) {
    constructor(baseCourse: BaseCourse) : this(
        id = baseCourse.id,
        department = baseCourse.department,
        courseNumber = baseCourse.courseNumber,
        name = baseCourse.name,
        description = baseCourse.description
    )
}
