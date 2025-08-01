import CourseCard from "../CourseCard";

const EnrolledCoursesCarousel = ({ courses }) => (
    <section className="text-center space-y-6">
        <div className="bg-gray-50 dark:bg-gray-900 shadow-md rounded-lg max-w-5xl mx-auto px-6 py-6 overflow-x-auto">
            <div className="flex gap-6 flex-nowrap pr-4">
                {courses.map((course) => (
                    <CourseCard
                        key={course.id}
                        code={course.code}
                        name={course.name}
                        notes={course.notes}
                        loggedIn={true}
                        width="77.5"
                    />
                ))}
            </div>
        </div>
    </section>
);

export default EnrolledCoursesCarousel;