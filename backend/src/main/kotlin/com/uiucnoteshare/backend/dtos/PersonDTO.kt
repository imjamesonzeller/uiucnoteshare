package com.uiucnoteshare.backend.dtos

import com.uiucnoteshare.backend.models.Person
import java.util.UUID

data class PersonDTO(
    val id: UUID?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val profilePicture: String?,
    val themePreference: String?,
) {
    constructor(person: Person) : this(
        id=person.id,
        firstName=person.firstName,
        lastName=person.lastName,
        email=person.email,
        profilePicture=person.profilePicture,
        themePreference=person.themePreference
    )
}