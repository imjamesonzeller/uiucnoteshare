package com.uiucnoteshare.backend.services

import org.springframework.stereotype.Service
import xyz.capybara.clamav.ClamavClient
import xyz.capybara.clamav.commands.scan.result.ScanResult
import java.nio.file.Path

@Service
class PDFVirusScanningService(
    private val clamavClient: ClamavClient = ClamavClient("localhost", 3310),
) {
    /**
     * Scans the given PDF file for viruses
     *
     * @param path the path to the PDF file to scan
     * @return true if the file is clean, false if a virus is detected.
     */
    fun scanForViruses(path: Path): Boolean {
        return when (val result = clamavClient.scan(path)) {
            is ScanResult.OK -> true
            is ScanResult.VirusFound -> false
            else -> throw IllegalStateException("Unexpected scan result: $result")
        }
    }
}