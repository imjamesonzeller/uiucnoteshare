package com.uiucnoteshare.backend.services

import com.uiucnoteshare.backend.config.CloudflareR2Client
import com.uiucnoteshare.backend.models.Note
import com.uiucnoteshare.backend.models.NoteUploadStatus
import com.uiucnoteshare.backend.repositories.NoteRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.nio.file.Path

@Service
class AsyncNoteProcessor(
    private val watermarkingService: PDFWatermarkService,
    private val cloudflareR2Client: CloudflareR2Client,
    private val noteRepository: NoteRepository,
) {
    @Async
    fun processUploadedNote(note: Note, pdfPath: Path) {
        watermarkingService.addWatermark(pdfPath)
        cloudflareR2Client.updateOrCreateObject("uiuc-note-share", "${note.id}.pdf", pdfPath)

        note.uploadStatus = NoteUploadStatus.UPLOADED
        noteRepository.save(note)
    }
}