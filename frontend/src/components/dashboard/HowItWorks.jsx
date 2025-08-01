const steps = [
    "Log in with @illinois.edu",
    "Enroll in your current courses",
    "Download and upload PDF notes easily",
    "Help others, improve your own notes",
];

const HowItWorks = () => (
    <section className="py-16">
        <div className="bg-gray-50 dark:bg-gray-900 rounded-lg shadow-md max-w-5xl mx-auto px-6 py-10">
            <h2 className="text-2xl font-semibold text-center mb-10">How It Works</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                {steps.map((text, idx) => (
                    <div
                        key={idx}
                        className="bg-white dark:bg-gray-800 shadow-sm rounded-lg p-6 flex flex-col items-center text-center space-y-2"
                    >
                        <div className="w-10 h-10 flex items-center justify-center rounded-full bg-indigo-600 text-white font-bold mb-2">
                            {idx + 1}
                        </div>
                        <p>{text}</p>
                    </div>
                ))}
            </div>
        </div>
    </section>
);

export default HowItWorks;