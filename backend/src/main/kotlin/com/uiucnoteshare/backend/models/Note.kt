package com.uiucnoteshare.backend.models

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "note")
data class Note(
    @Id
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: Person,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_offering_id", nullable = true)
    var course: CourseOffering,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var caption: String? = null,

    @Column(nullable = false)
    var fileUploaded: Boolean = false,

    var createdAt: LocalDateTime? = null,
)
