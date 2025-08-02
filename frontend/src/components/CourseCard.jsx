import { Card, CardHeader, CardBody, Button } from "@heroui/react";

const CourseCard = ({ code, name, notes, loggedIn, width = 64 }) => {
    const widthClasses = {
        64: "w-64",
        77.5: "w-77.5",
        80: "w-80",
    };

    return (
        <Card
            className={`${widthClasses[width] || "w-64" } flex-shrink-0 py-4 bg-white dark:bg-gray-900 shadow-md flex flex-col justify-between transition-transform duration-200 transform hover:shadow-lg hover:scale-[1.02]`}
        >
            <CardHeader className="font-semibold">{code}</CardHeader>
            <CardBody className="space-y-2">
                <p className="font-medium text-lg">{name}</p>
                <p className="text-sm text-gray-500 mb-2">
                    Notes available: {notes}
                </p>
                {loggedIn ? (
                    <Button size="sm" color="primary" className="mt-2">
                        View Notes
                    </Button>
                ) : (
                    <Button size="sm" className="mt-2">
                        Login to View Notes
                    </Button>
                )}
            </CardBody>
        </Card>
    );
};

export default CourseCard;