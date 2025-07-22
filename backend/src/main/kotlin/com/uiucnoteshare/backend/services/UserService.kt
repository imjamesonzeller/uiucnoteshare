package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.repositories.PersonRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val personRepository: PersonRepository,
) {
    fun findOrCreateFromMicrosoft(email: String, microsoftOid: String, fullName: String): Person {
        val existingByOld = personRepository.findByMicrosoftOid(microsoftOid)
        if (existingByOld != null) return existingByOld

        val existingByEmail = personRepository.findByEmail(email)
        if (existingByEmail != null) return existingByEmail

        val (firstName, lastName) = splitFullName(fullName)

        val newPerson = Person(
            email = email,
            firstName = firstName,
            lastName = lastName,
            microsoftOid = microsoftOid,
            isAdmin = false,
            profilePicture = null,
            themePreference = null
        )

        return personRepository.save(newPerson)
    }

    private fun splitFullName(fullName: String): Pair<String, String> {
        val parts = fullName.trim().split(",")
        return when (parts.size) {
            2 -> {
                val lastName = parts[0].trim()
                val firstName = parts[1].trim()
                firstName to lastName
            }
            1 -> parts[0].trim() to ""
            else -> "Unknown" to ""
        }
    }
}