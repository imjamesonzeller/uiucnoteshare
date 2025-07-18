package com.uiucnoteshare.backend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "person")
data class Person(
    @Id
    var id: UUID? = null, // <--- DB generates UUIDS

    @Column(nullable = false, length = 255, unique = true)
    var microsoftOid: String,

    @Column(nullable = false, length = 100)
    var firstName: String,

    @Column(nullable = false, length = 100)
    var lastName: String,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(columnDefinition = "TEXT")
    var profilePicture: String? = null,

    @Column(length = 50)
    var themePreference: String? = null,

    var isAdmin: Boolean = false
)
