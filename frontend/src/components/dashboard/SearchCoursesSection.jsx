import { Input } from "@heroui/react";
import CourseCard from "../CourseCard";

const SearchCoursesSection = ({ popularCourses }) => (
    <section className="text-center space-y-6">
        <h2 className="text-2xl font-semibold">Search or Browse Courses</h2>
        <div className="max-w-md mx-auto">
            <Input placeholder='Search "CS 124", "MATH 241", etc.' />
        </div>
        <div>
            <p className="mb-3 font-medium">📚 Most Popular Classes:</p>
            <div className="flex justify-center gap-4 flex-wrap">
                {popularCourses.map((course) => (
                    <CourseCard
                        key={course.id}
                        code={course.code}
                        name={course.name}
                        notes={course.notes}
                        loggedIn={false}
                    />
                ))}
            </div>
        </div>
    </section>
);

export default SearchCoursesSection;