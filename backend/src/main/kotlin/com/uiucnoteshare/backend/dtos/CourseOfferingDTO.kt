package com.uiucnoteshare.backend.dtos

import com.uiucnoteshare.backend.models.CourseOffering
import java.util.*

data class CourseOfferingDTO(
    val id: UUID?,
    val semester: String,
    val classCode: String
) {
    constructor(courseOffering: CourseOffering) : this(
        id = courseOffering.id,
        semester = courseOffering.semester,
        classCode = courseOffering.classCode
    )
}
