import UploadProgressModal from "../components/course/UploadProgressModal";
import { useEffect, useState } from "react";
import { Button, Dropdown, DropdownTrigger, DropdownMenu, DropdownItem, Card, Spinner, Modal, ModalContent, ModalHeader, ModalBody, ModalFooter, Avatar, Input, Textarea, Select, SelectItem } from "@heroui/react";
import { useParams } from "react-router-dom";

const currentUserId = "user-123"; // placeholder current user id

export default function CoursePage() {
    const { baseCourseId } = useParams();
    const [loggedIn, setLoggedIn] = useState(true); // placeholder
    const [enrolled, setEnrolled] = useState(true); // placeholder
    const [semester, setSemester] = useState("Fall 2025");
    const [semesters] = useState(["Fall 2025", "Spring 2025", "All Semesters"]);
    const [notes] = useState([
        { title: "Note Title", author: "Author", uploaded: "2 days ago" },
        { title: "Note Title", author: "Author", uploaded: "2 days ago" },
        { title: "Note Title", author: "Author", uploaded: "2 days ago" },
        { title: "Note Title", author: "Author", uploaded: "2 days ago" },
    ]);
    const [loading, setLoading] = useState(true);
    const [hasMore, setHasMore] = useState(true);
    const [selectedNote, setSelectedNote] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [loadingNote, setLoadingNote] = useState(false);
    const [showLoginPrompt, setShowLoginPrompt] = useState(false);
    const [showUploadModal, setShowUploadModal] = useState(false);
    const [uploadTitle, setUploadTitle] = useState("");
    const [uploadCaption, setUploadCaption] = useState("");
    const [uploadFile, setUploadFile] = useState(null);
    const [uploadCourseOffering, setUploadCourseOffering] = useState("offering-1"); // placeholder current offering id
    const [enrolledCourses, setEnrolledCourses] = useState([]);
    const [uploadErrors, setUploadErrors] = useState({ title: false, file: false, offering: false });

    const [showProgressModal, setShowProgressModal] = useState(false);
    const [progressPayload, setProgressPayload] = useState(null);

    // Simulate fetching user's enrolled courses
    useEffect(() => {
        // Placeholder: In real app, fetch("/api/me/enrollments?onlyActive=true")
        const fakeCourses = [
            { id: "offering-1", code: "CS 124" },
            { id: "offering-2", code: "MATH 241" },
            { id: "offering-3", code: "STAT 400" },
        ];
        setEnrolledCourses(fakeCourses);
        // Set default to current course offering if found
        setUploadCourseOffering("offering-1"); // Simulate current offering id
    }, []);

    // Placeholder fetch logic
    useEffect(() => {
        setLoading(true);
        console.log("Fetch course and notes for:", baseCourseId, "Semester:", semester);
        setTimeout(() => {
            setLoading(false);
        }, 1000); // simulate fetch
    }, [baseCourseId, semester]);

    const handleNoteClick = async (noteId) => {
        if (!loggedIn) {
            setShowLoginPrompt(true);
            return;
        }

        setIsModalOpen(true);
        setLoadingNote(true);
        try {
            // Placeholder fetch: in real app, fetch(`/api/notes/${noteId}`)
            const fetched = {
                id: noteId,
                title: "Full Note Example",
                caption: "This is a sample caption for the note.",
                fileUrl: "https://ontheline.trincoll.edu/images/bookdown/sample-local-pdf.pdf",
                createdAt: new Date().toISOString(),
                author: {
                    id: currentUserId,
                    firstName: "Alice",
                    lastName: "Smith",
                    profilePicture: null
                },
                semester: "Fall 2025",
                classCode: "CS 124"
            };
            setSelectedNote(fetched);
        } finally {
            setLoadingNote(false);
        }
    };

    return (
        <div className="px-6 py-8 max-w-5xl mx-auto">
            <div className="flex items-center justify-between mb-1">
                <h1 className="text-3xl font-bold">CS 124 – Introduction to Computer Science</h1>
                {loggedIn && semester === "Fall 2025" && (
                    <Button color={enrolled ? "danger" : "primary"} variant="flat">
                        {enrolled ? "Unenroll" : "Enroll"}
                    </Button>
                )}
            </div>
            <p className="text-gray-600 mb-4">Department of Computer Science</p>

            <div className="flex items-center justify-between mb-6">
                <p className="text-gray-700">{notes.length} notes available</p>

                <div className="flex gap-3 items-center">
                    {loggedIn && enrolled && semester === "Fall 2025" && (
                        <Button color="primary" onPress={() => setShowUploadModal(true)}>Upload Note</Button>
                    )}

                    <Dropdown>
                        <DropdownTrigger>
                            <Button variant="bordered">{semester}</Button>
                        </DropdownTrigger>
                        <DropdownMenu
                            aria-label="Semester Selector"
                            onAction={(key) => setSemester(key)}
                        >
                            {semesters.map((sem) => (
                                <DropdownItem key={sem}>{sem}</DropdownItem>
                            ))}
                        </DropdownMenu>
                    </Dropdown>
                </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {loading ? (
                    <div className="col-span-2 flex justify-center py-10">
                        <Spinner label="Loading notes..." />
                    </div>
                ) : (
                    notes.map((note, index) => (
                        <Card
                            key={index}
                            isPressable
                            shadow="sm"
                            className="p-4 cursor-pointer hover:shadow-lg hover:scale-[1.02] transition-transform items-start justify-start text-left"
                            onPress={() => handleNoteClick(index)} // using index as fake id
                        >
                            <h3 className="font-semibold">{note.title}</h3>
                            <p className="text-gray-500 text-sm">{note.author}</p>
                            <p className="text-gray-400 text-xs">{note.uploaded}</p>
                        </Card>
                    ))
                )}
            </div>

            {!loading && hasMore && (
                <div className="flex justify-center mt-6">
                    <Button variant="flat" onPress={() => console.log("Load more notes")}>
                        Load More
                    </Button>
                </div>
            )}

            <Modal isOpen={isModalOpen} onOpenChange={setIsModalOpen} size="5xl" scrollBehavior="inside" hideCloseButton={true}>
                <ModalContent className="overflow-hidden">
                    {(onClose) => (
                        <div className="flex flex-col h-[80vh]">

                            {/* Header row with title and actions */}
                            <div className="flex items-center justify-between px-4 py-2 border-b">
                                <h3 className="text-lg font-semibold">{selectedNote?.title}</h3>
                                <div className="flex items-center gap-2">
                                    {selectedNote?.fileUrl && (
                                        <a
                                            href={selectedNote.fileUrl}
                                            download
                                            target="_blank"
                                            rel="noopener noreferrer"
                                        >
                                            {/* TODO: Wire this button to /notes/:id/download to download the note*/}
                                            <Button color="primary" size="sm" variant="flat">
                                                Download
                                            </Button>
                                        </a>
                                    )}
                                    <Dropdown>
                                        <DropdownTrigger>
                                            <Button isIconOnly variant="light" size="sm">
                                                ⋮
                                            </Button>
                                        </DropdownTrigger>
                                        <DropdownMenu>
                                            {selectedNote?.author?.id === currentUserId && (
                                                <DropdownItem key="delete" className="text-danger">
                                                    Delete
                                                </DropdownItem>
                                            )}
                                            <DropdownItem key="info">More Info</DropdownItem>
                                        </DropdownMenu>
                                    </Dropdown>
                                    <Button isIconOnly variant="light" size="sm" onPress={onClose}>
                                        ✕
                                    </Button>
                                </div>
                            </div>

                            <div className="flex flex-1 flex-col lg:flex-row">

                                {/* Left: PDF Viewer */}
                                <div className="flex-1 bg-gray-50 flex items-center justify-center">
                                    {loadingNote || !selectedNote ? (
                                        <Spinner />
                                    ) : (
                                        <iframe
                                            src={selectedNote.fileUrl}
                                            title={selectedNote.title}
                                            className="w-full h-full"
                                        />
                                    )}
                                </div>

                                {/* Right: Info Sidebar */}
                                <div className="w-full lg:w-80 p-4 border-t lg:border-t-0 lg:border-l flex flex-col justify-between rounded-b-lg lg:rounded-b-none lg:rounded-r-lg bg-white">
                                    <div>
                                        {selectedNote?.caption && (
                                            <p className="text-gray-600">{selectedNote.caption}</p>
                                        )}
                                    </div>

                                    <div className="flex items-center gap-3 mt-4">
                                        <Avatar
                                            src={selectedNote?.author?.profilePicture ?? ""}
                                            name={`${selectedNote?.author?.firstName ?? ""} ${selectedNote?.author?.lastName ?? ""}`}
                                        />
                                        <div>
                                            <p className="text-sm font-medium">
                                                {selectedNote?.author?.firstName} {selectedNote?.author?.lastName}
                                            </p>
                                            <p className="text-xs text-gray-400">
                                                Uploaded {selectedNote?.createdAt
                                                    ? new Date(selectedNote.createdAt).toLocaleString()
                                                    : "Unknown"}
                                            </p>
                                            <p className="text-xs text-gray-400">
                                                {selectedNote?.classCode} – {selectedNote?.semester}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </ModalContent>
            </Modal>

            {/* Login Prompt Modal */}
            <Modal isOpen={showLoginPrompt} onOpenChange={setShowLoginPrompt}>
                <ModalContent>
                    {(onClose) => (
                        <div className="p-6 text-center">
                            <h3 className="text-lg font-semibold mb-2">Login Required</h3>
                            <p className="text-gray-600 mb-4">You must be logged in to view this note.</p>
                            <Button color="primary" onPress={() => {
                                onClose();
                                console.log("Redirect to login page");
                            }}>
                                Login
                            </Button>
                        </div>
                    )}
                </ModalContent>
            </Modal>

            {/* Upload Note Modal */}
            <Modal
                isOpen={showUploadModal}
                onOpenChange={(isOpen) => {
                    setShowUploadModal(isOpen);
                    if (!isOpen) {
                        setUploadTitle("");
                        setUploadCaption("");
                        setUploadFile(null);
                        setUploadCourseOffering("offering-1");
                        setUploadErrors({ title: false, file: false, offering: false });
                    }
                }}
            >
                <ModalContent>
                    {(onClose) => (
                        <div className="p-6 space-y-4">
                            <h3 className="text-lg font-semibold mb-2">Upload Note</h3>

                            <Input
                                label="Title"
                                placeholder="Enter note title"
                                fullWidth
                                isInvalid={uploadErrors.title}
                                errorMessage={uploadErrors.title ? "Title is required." : ""}
                                value={uploadTitle}
                                onChange={(e) => setUploadTitle(e.target.value)}
                            />

                            <Textarea
                                label="Caption"
                                placeholder="Optional caption"
                                fullWidth
                                value={uploadCaption}
                                onChange={(e) => setUploadCaption(e.target.value)}
                            />

                            {/* File Upload */}
                            <div className="flex flex-col gap-2">
                                <span className="text-sm font-medium text-gray-600">PDF File</span>
                                <Button
                                    as="label"
                                    color={uploadErrors.file ? "danger" : "primary"}
                                    variant="flat"
                                    className="w-fit"
                                >
                                    Choose File
                                    <input
                                        type="file"
                                        hidden
                                        accept=".pdf,application/pdf"
                                        onChange={(e) => {
                                            const file = e.target.files[0];
                                            if (!file) return;

                                            const isPdf =
                                                file.type === "application/pdf" ||
                                                file.name.toLowerCase().endsWith(".pdf");

                                            if (isPdf && file.size <= 5 * 1024 * 1024) {
                                                setUploadFile(file);
                                            } else {
                                                alert("Only PDF files under 5MB are allowed.");
                                                e.target.value = null;
                                            }
                                        }}
                                    />
                                </Button>
                                {uploadFile && <p className="text-sm text-gray-500">{uploadFile.name}</p>}
                                {uploadErrors.file && <p className="text-sm text-red-500">A PDF file is required.</p>}
                            </div>

                            {/* Course Offering Dropdown */}
                            <Select
                                label="Course Offering"
                                selectedKeys={[uploadCourseOffering]}
                                onSelectionChange={(keys) => setUploadCourseOffering([...keys][0])}
                                className="w-full"
                                isInvalid={uploadErrors.offering}
                                errorMessage={uploadErrors.offering ? "Select a course offering." : ""}
                            >
                                {enrolledCourses.map((course) => (
                                    <SelectItem key={course.id}>{course.code}</SelectItem>
                                ))}
                            </Select>

                            <div className="flex justify-end gap-2 pt-4">
                                <Button variant="flat" onPress={onClose}>Cancel</Button>
                                <Button
                                    color="primary"
                                    onPress={() => {
                                        const newErrors = {
                                            title: !uploadTitle.trim(),
                                            file: !uploadFile,
                                            offering: !uploadCourseOffering,
                                        };
                                        setUploadErrors(newErrors);

                                        const hasError = Object.values(newErrors).some(Boolean);
                                        if (hasError) return;

                                        // Close the upload modal and open progress modal
                                        setUploadErrors({ title: false, file: false, offering: false });
                                        onClose();

                                        // Pass payload to progress modal
                                        setProgressPayload({
                                            file: uploadFile,
                                            title: uploadTitle,
                                            caption: uploadCaption,
                                            courseOffering: uploadCourseOffering,
                                        });
                                        setShowProgressModal(true);
                                    }}
                                >
                                    Upload
                                </Button>
                            </div>
                        </div>
                    )}
                </ModalContent>
            </Modal>

            {/* Upload Progress Modal */}
            {progressPayload && (
                <UploadProgressModal
                    isOpen={showProgressModal}
                    onClose={() => setShowProgressModal(false)}
                    file={progressPayload.file}
                    title={progressPayload.title}
                    caption={progressPayload.caption}
                    courseOffering={progressPayload.courseOffering}
                />
            )}
        </div>
    );
}
