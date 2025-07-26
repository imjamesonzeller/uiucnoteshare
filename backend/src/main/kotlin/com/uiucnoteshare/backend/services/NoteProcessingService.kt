package com.uiucnoteshare.backend.services

import org.springframework.stereotype.Service
import java.nio.file.Path

@Service
class NoteProcessingService(
    private val pdfVirusScanner: PDFVirusScanningService,
    private val pdfNSFWScanner: PDFNSFWScanningService,
    private val hateSpeechDetectionService: HateSpeechDetectionService
) {
    fun isSafeNote(notePdfPath: Path): Pair<Boolean, String?> {
        val safeFromViruses = pdfVirusScanner.scanForViruses(notePdfPath)
        if (!safeFromViruses) {
            return Pair(false, "virus")
        }

        val safeFromNSFW = pdfNSFWScanner.scanForNSFW(notePdfPath)
        if (!safeFromNSFW) {
            return Pair(false, "nsfw")
        }

        val containsHateSpeech = hateSpeechDetectionService.containsHateSpeech(notePdfPath)
        if (containsHateSpeech) {
            return Pair(false, "hate")
        }

        return Pair(true, null)
    }
}