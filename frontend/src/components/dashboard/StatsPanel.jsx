import { Card, CardBody, CardHeader, Button } from "@heroui/react";

const StatsPanel = ({ stats }) => (
    <Card className="w-full md:w-96 bg-gray-50 dark:bg-gray-900 shadow-md rounded-lg text-center flex flex-col justify-between">
        <CardHeader className="font-semibold">Your Stats</CardHeader>
        <CardBody className="flex flex-col justify-between flex-1 space-y-4 py-6">
            <div className="space-y-2">
                <p>Notes Uploaded: {stats.uploaded}</p>
                <p>Notes Downloaded: {stats.downloaded}</p>
                <p>Courses Enrolled: {stats.enrolled}</p>
            </div>
            <Button color="primary" className="mt-4 w-full">
                Quick Upload
            </Button>
        </CardBody>
    </Card>
);

export default StatsPanel;