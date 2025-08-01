import { Button } from "@heroui/react";

const HeroLoggedOut = () => (
    <section className="text-center py-20 mb-0">
        <h1 className="text-5xl font-bold mb-4">UIUC Note Share</h1>
        <p className="text-xl mb-6">
            Share and explore student-made notes for your classes.
        </p>
        <div className="flex justify-center gap-4">
            <Button color="primary">Login with @illinois.edu</Button>
            <Button variant="bordered">Browse All Courses</Button>
        </div>
    </section>
);

export default HeroLoggedOut;