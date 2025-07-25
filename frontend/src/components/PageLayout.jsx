import NavigationBar from './NavigationBar'
import Footer from './Footer'
import HeroUINavBar from "./HeroUINavBar.jsx";

const PageLayout = ({ children }) => {
    return (
        <div className="flex flex-col min-h-screen bg-white text-gray-900 dark:bg-gray-950 dark:text-white">
            <HeroUINavBar />

            <main className="flex-grow px-4 py-6 md:px-8 lg:px-16">
                {children}
            </main>

            <Footer />
        </div>
    )
}

export default PageLayout