package com.uiucnoteshare.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Transient
import java.util.*

@Entity
@Table(name = "base_course")
data class BaseCourse(
    @Id
    var id: UUID? = null,

    @Column(nullable = false, length = 50)
    var department: String,

    @Column(nullable = false, length = 20)
    var courseNumber: String,

    @Column(nullable = false, length = 255)
    var name: String,

    var description: String? = null,
) {
    @get:Transient
    val fullCode: String
        get() = "$department $courseNumber"
}