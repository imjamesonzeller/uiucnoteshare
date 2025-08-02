import {useState, useEffect, useCallback} from "react";
import {Button, Modal, ModalContent, Spinner} from "@heroui/react";

function UploadProgressModal({ isOpen, onClose, file, title, caption, courseOffering }) {
    const [currentStep, setCurrentStep] = useState(0);
    const [errorMessage, setErrorMessage] = useState("");
    const steps = ["Preparing Upload", "Uploading File", "Scanning File"];

    const startUpload = useCallback(async () => {
        setErrorMessage(""); // reset any previous errors
        try {
            // Step 1: Prepare
            setCurrentStep(0);
            const metadataResponse = await fetch("/api/notes", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ title, caption, courseOffering, fileName: file.name })
            });

            if (!metadataResponse.ok) throw new Error("Failed to prepare upload.");
            const { noteId, presignedUrl } = await metadataResponse.json();

            setCurrentStep(1);

            // Step 2: Upload file to R2
            const uploadResponse = await fetch(presignedUrl, {
                method: "PUT",
                body: file
            });
            if (!uploadResponse.ok) throw new Error("File upload failed.");
            setCurrentStep(2);

            // Step 3: Confirm upload & scan
            const confirmResponse = await fetch(`/api/notes/${noteId}/confirm-upload`, { method: "POST" });

            if (confirmResponse.status === 409) {
                const text = await confirmResponse.text(); // The backend includes reason in the message
                setErrorMessage(text || "Your note is pending manual review due to a possible policy violation.");
                return; // Stop here, show error to user
            } else if (!confirmResponse.ok) {
                throw new Error("An unknown error occurred during scanning.");
            }

            // ✅ Success: close modal after short delay
            setTimeout(() => onClose(), 800);

        } catch (err) {
            console.error(err);
            setErrorMessage(err.message || "Upload failed. Please try again.");
        }
    }, [file, title, caption, courseOffering, onClose]);

    useEffect(() => {
        if (isOpen && file) {
            startUpload();
        }
    }, [isOpen, file, startUpload]);

    return (
        <Modal isOpen={isOpen} onOpenChange={onClose} hideCloseButton>
            <ModalContent className={"w-auto"}>
                <div className="p-6 space-y-6 w-96">
                    <h3 className="text-lg font-semibold">Uploading Note</h3>

                    <div className="space-y-3">
                        {steps.map((step, index) => (
                            <div key={index} className="flex items-center gap-3">
                                {currentStep > index ? (
                                    <span className="text-green-500 font-bold">✓</span>
                                ) : currentStep === index && !errorMessage ? (
                                    <Spinner size="sm" />
                                ) : (
                                    <span className="w-3 h-3 border rounded-full border-gray-300" />
                                )}
                                <span className={currentStep >= index ? "text-black" : "text-gray-400"}>
                  {step}
                </span>
                            </div>
                        ))}
                    </div>

                    {errorMessage && (
                        <p className="text-red-500 text-sm mt-2">{errorMessage}</p>
                    )}

                    <div className="flex justify-end pt-4">
                        <Button
                            color={errorMessage ? "danger" : "primary"}
                            variant={errorMessage ? "flat" : "solid"}
                            onPress={onClose}
                        >
                            {errorMessage ? "Close" : "Done"}
                        </Button>
                    </div>
                </div>
            </ModalContent>
        </Modal>
    );
}

export default UploadProgressModal;