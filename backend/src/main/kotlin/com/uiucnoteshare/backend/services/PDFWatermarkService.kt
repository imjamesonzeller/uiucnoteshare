package com.uiucnoteshare.backend.services

import org.springframework.stereotype.Service

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import java.nio.file.Path
import java.io.IOException

@Service
class PDFWatermarkService {
    fun addWatermark(inputPath: Path) {
        try {
            val document = Loader.loadPDF(inputPath.toFile())
            document.use { doc ->
                for (page in doc.pages) {
                    if (page is PDPage) {
                        addWatermarkToPage(doc, page)
                    }
                }
                doc.save(inputPath.toFile())
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to add watermark to PDF at $inputPath", e)
        }
    }

    private fun addWatermarkToPage(doc: org.apache.pdfbox.pdmodel.PDDocument, page: PDPage, text: String = "Downloaded from UIUC Note Share") {
        val mediaBox: PDRectangle = page.mediaBox
        val stream = PDPageContentStream(
            doc,
            page,
            PDPageContentStream.AppendMode.APPEND,
            true,
            true
        )
        val marginX = 50f
        val marginY = 30f
        val font = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
        val fontSize = 12f
        val textWidth = font.getStringWidth(text) / 1000 * fontSize
        val textX = mediaBox.upperRightX - marginX - textWidth.coerceAtLeast(0f)

        stream.beginText()
        stream.setFont(font, fontSize)
        stream.setNonStrokingColor(150F, 150F, 150F)
        stream.newLineAtOffset(textX, marginY)
        stream.showText(text)
        stream.endText()
        stream.close()
    }
}