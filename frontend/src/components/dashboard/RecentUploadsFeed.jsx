import { Card, CardBody, CardHeader, Button } from "@heroui/react";

const RecentUploadsFeed = ({ notes }) => (
    <Card className="flex-1 w-full flex flex-col justify-start bg-gray-50 dark:bg-gray-900 shadow-md">
        <CardHeader className="text-2xl font-semibold text-center md:text-left">Recent Uploads</CardHeader>
        <CardBody className="flex-1">
            <ul className="space-y-3">
                {notes.map((note) => (
                    <li
                        key={note.id}
                        className="flex justify-between items-center p-3 rounded-lg bg-white dark:bg-gray-800 shadow-sm"
                    >
                        <span>{note.courseCode}: {note.title}</span>
                        <Button size="sm">View</Button>
                    </li>
                ))}
            </ul>
        </CardBody>
    </Card>
);

export default RecentUploadsFeed;