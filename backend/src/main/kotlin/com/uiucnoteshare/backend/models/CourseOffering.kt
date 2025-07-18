package com.uiucnoteshare.backend.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "course_offering")
data class CourseOffering(
    @Id
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_course_id", nullable = false)
    var baseCourse: BaseCourse,

    @Column(nullable = false, length = 50)
    var semester: String,

    @Column(nullable = false, length = 255)
    var classCode: String,
)