package com.uiucnoteshare.backend.dtos

import com.uiucnoteshare.backend.models.BaseCourse
import com.uiucnoteshare.backend.models.CourseOffering
import com.uiucnoteshare.backend.models.Enrollment
import java.util.*

data class EnrolledCourseOfferingDTO(
    val courseOfferingId: UUID?,
    val semester: String,
    val classCode: String,
    val baseCourseId: UUID?,
    val department: String,
    val courseNumber: String,
    val courseName: String
) {
    constructor(offering: CourseOffering, baseCourse: BaseCourse) : this(
        courseOfferingId = offering.id,
        semester = offering.semester,
        classCode = offering.classCode,
        baseCourseId = baseCourse.id,
        department = baseCourse.department,
        courseNumber = baseCourse.courseNumber,
        courseName = baseCourse.name
    )
    companion object {
        fun fromEnrollment(enrollment: Enrollment): EnrolledCourseOfferingDTO {
            val offering = enrollment.course
            val baseCourse = offering.baseCourse
            return EnrolledCourseOfferingDTO(offering, baseCourse)
        }
    }
}
