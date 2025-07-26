package com.uiucnoteshare.backend.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.file.Path

@Service
class HateSpeechDetectionService {
    private val blockedWords: Set<String> = loadBlockedWords()

    private fun loadBlockedWords(): Set<String> {
        val mapper = jacksonObjectMapper()
        val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream("blocked_words.json")
            ?: throw IllegalStateException("Could not load blocked_words.json")

        val json = mapper.readTree(inputStream)
        return json["blockedWords"].map { it.asText() }.toSet()
    }

    fun containsHateSpeech(path: Path): Boolean {
        val text = Loader.loadPDF(path.toFile()).use { doc ->
            PDFTextStripper().getText(doc).lowercase()
        }

        return blockedWords.any { word ->
            Regex("\\b${Regex.escape(word)}\\b").containsMatchIn(text)
        }
    }
}