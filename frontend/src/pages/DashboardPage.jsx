import { useAuth } from "../context/AuthContext.jsx";
import HeroLoggedOut from "../components/dashboard/HeroLoggedOut.jsx";
import HeroLoggedIn from "../components/dashboard/HeroLoggedIn.jsx";
import SearchCoursesSection from "../components/dashboard/SearchCoursesSection.jsx";
import HowItWorks from "../components/dashboard/HowItWorks.jsx";
import EnrolledCoursesCarousel from "../components/dashboard/EnrolledCoursesCarousel.jsx";
import StatsPanel from "../components/dashboard/StatsPanel.jsx";
import RecentUploadsFeed from "../components/dashboard/RecentUploadsFeed.jsx";

const DashboardPage = () => {
  const { user, loading } = useAuth();

  const enrolledCourses = [
    { id: 101, code: "CS 225", name: "Data Structures", notes: 5 },
    { id: 102, code: "STAT 400", name: "Statistics & Probability I", notes: 2 },
    { id: 102, code: "STAT 400", name: "Statistics & Probability I", notes: 2 },
    { id: 102, code: "STAT 400", name: "Statistics & Probability I", notes: 2 },
  ];

  const popularCourses = [
    { id: 1, code: "CS 124", name: "Intro to CS", notes: 12 },
    { id: 2, code: "MATH 241", name: "Calc III", notes: 8 },
    { id: 3, code: "PHYS 211", name: "Mechanics", notes: 3 },
  ];

  const recentNotes = [
    { id: 1, courseCode: "CS 225", title: "Week 3 Lecture Notes" },
    { id: 2, courseCode: "STAT 400", title: "Chapter 2 Homework Solutions" },
    { id: 3, courseCode: "CS 225", title: "Lab 4 Notes" },
  ];

  const userStats = {
    uploaded: 3,
    downloaded: 15,
    enrolled: enrolledCourses.length,
  };

  if (loading) {
    return <div className="text-center py-20">Loading...</div>;
  }

  return (
      <>
        {!user ? (
            <>
              <HeroLoggedOut />
              <SearchCoursesSection popularCourses={popularCourses} />
              <HowItWorks />
            </>
        ) : (
            <>
              <HeroLoggedIn user={user} />
              <EnrolledCoursesCarousel courses={enrolledCourses} />
              <section className="mt-12 flex flex-col md:flex-row justify-center items-stretch gap-8 max-w-5xl mx-auto">
                <StatsPanel stats={userStats} />
                <RecentUploadsFeed notes={recentNotes} />
              </section>
            </>
        )}
      </>
  );
};

export default DashboardPage;