import { Card, CardHeader, CardBody, Button } from "@heroui/react";

const CourseCard = ({ code, name, notes, loggedIn, width = "64" }) => {
    return (
        <Card
            className={`w-${width} flex-shrink-0 py-4 bg-white dark:bg-gray-900 shadow-md flex flex-col justify-between transition-transform duration-200 transform hover:-translate-y-1 hover:shadow-lg`}
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