const HeroLoggedIn = ({ user }) => (
    <section className="text-center pt-10 mb-0 pb-10">
        <h1 className="text-4xl font-bold mb-4">
            Hey, {user.firstName}. Welcome back!
        </h1>
        <p className="text-lg mb-6">Here are your enrolled courses:</p>
    </section>
);

export default HeroLoggedIn;