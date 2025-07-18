package com.uiucnoteshare.backend.repositories

import com.uiucnoteshare.backend.models.Person
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PersonRepository : JpaRepository<Person, UUID>{
    fun findByEmail(email: String): Person?
    fun existsByEmail(email: String): Boolean
    fun findByMicrosoftOid(oid: String): Person?
}