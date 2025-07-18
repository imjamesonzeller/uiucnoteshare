package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.dtos.CourseOfferingDTO
import com.uiucnoteshare.backend.dtos.NoteAuthorDTO
import com.uiucnoteshare.backend.dtos.NoteDTO
import com.uiucnoteshare.backend.models.CourseOffering
import com.uiucnoteshare.backend.models.Enrollment
import com.uiucnoteshare.backend.models.Note
import com.uiucnoteshare.backend.models.Person
import com.uiucnoteshare.backend.repositories.CourseOfferingRepository
import com.uiucnoteshare.backend.repositories.EnrollmentRepository
import com.uiucnoteshare.backend.repositories.NoteRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class CourseOfferingService(
    private val courseOfferingRepository: CourseOfferingRepository,
    private val noteRepository: NoteRepository,
    private val enrollmentRepository: EnrollmentRepository
) {
    fun getOfferings(semester: String?): List<CourseOfferingDTO> {
        val offerings = if (semester != null) {
            courseOfferingRepository.findAllBySemester(semester)
        } else {
            courseOfferingRepository.findAll()
        }

        return offerings.map { CourseOfferingDTO(it) }
    }

    fun getOffering(offeringId: UUID): CourseOfferingDTO? {
        val offering = courseOfferingRepository.findByIdOrNull(offeringId) ?: return null
        return CourseOfferingDTO(offering)
    }

    fun getNotes(offeringId: UUID): List<NoteDTO?> {
        val notes = noteRepository.findAllByCourseId(offeringId)
        return notes.map { it.toNoteDTO() }
    }

    fun Note.toNoteDTO(): NoteDTO {
        val author = this.author
        val offering = this.course

        return NoteDTO(
            id = this.id,
            title = this.title,
            caption = this.caption,
            fileUrl = this.fileUrl,
            createdAt = this.createdAt,
            author = NoteAuthorDTO(
                id = author.id!!,
                firstName = author.firstName,
                lastName = author.lastName,
                profilePicture = author.profilePicture
            ),
            courseOfferingId = offering.id,
            semester = offering.semester,
            classCode = offering.classCode
        )
    }

    fun enrollUser(person: Person, offeringId: UUID): Enrollment {
        val offering = courseOfferingRepository.findByIdOrNull(offeringId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Course offering not found")

        val existingEnrollment = enrollmentRepository.findByPersonIdAndCourseId(person.id!!, offeringId)
        if (existingEnrollment != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User is already enrolled in this course offering")
        }

        val enrollment = Enrollment(
            person = person,
            course = offering
        )

        return enrollmentRepository.save(enrollment)
    }

    fun unenrollUser(person: Person, offeringId: UUID) {
        val enrollment = enrollmentRepository.findByPersonIdAndCourseId(person.id!!, offeringId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found")

        enrollmentRepository.delete(enrollment)
    }
}