package com.uiucnoteshare.backend.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "enrollment")
data class Enrollment(
    @Id
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    var person: Person,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_offering_id", nullable = false)
    var course: CourseOffering,
)