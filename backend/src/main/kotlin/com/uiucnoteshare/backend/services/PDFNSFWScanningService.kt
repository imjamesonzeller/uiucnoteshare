package com.uiucnoteshare.backend.services

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.stereotype.Service
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder
import com.amazonaws.services.rekognition.model.*
import org.springframework.beans.factory.annotation.Value
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.file.Path
import javax.imageio.ImageIO

@Service
class PDFNSFWScanningService(
    @Value("\${aws.rekognition.access-key}")
    private var accessKey: String,

    @Value("\${aws.rekognition.secret-key}")
    private var secretKey: String
) {
    /**
     * Scans the given PDF file for NSFW content
     *
     * @param path the path to the PDF file to scan
     * @return true if the file is clean, false if NSFW is detected.
     */
    fun scanForNSFW(path: Path): Boolean {
        val credentials = BasicAWSCredentials(accessKey, secretKey)
        val rekognitionClient = AmazonRekognitionClientBuilder.standard()
            .withRegion("us-east-1")
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build()

        val document = Loader.loadPDF(path.toFile())

        document.use { doc ->
            val renderer = PDFRenderer(doc)

            for (page in 0 until doc.numberOfPages) {
                val image: BufferedImage = renderer.renderImageWithDPI(page, 150f, ImageType.RGB)

                val imageBytes = ByteArrayOutputStream().use { baos ->
                    ImageIO.write(image, "jpg", baos)
                    baos.toByteArray()
                }

                val request = DetectModerationLabelsRequest()
                    .withImage(Image().withBytes(ByteBuffer.wrap(imageBytes)))
                    .withMinConfidence(80F)

                val result: DetectModerationLabelsResult = rekognitionClient.detectModerationLabels(request)
                val labels = result.moderationLabels

                if (labels.any {
                    it.name.contains("Explicit", ignoreCase = true) ||
                    it.name.contains("Nudity", ignoreCase = true)
                }) {
                    return false
                }
            }
        }
        return true
    }
}