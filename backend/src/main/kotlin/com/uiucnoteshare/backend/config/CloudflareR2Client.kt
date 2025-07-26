package com.uiucnoteshare.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.core.sync.ResponseTransformer
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration

@Component
class CloudflareR2Client(
    @Value("\${cloudflare.r2.account-id}")
    private var accountId: String,

    @Value("\${cloudflare.r2.access-key}")
    private var accessKey: String,

    @Value("\${cloudflare.r2.secret-key}")
    private var secretKey: String
) {

    private val endpoint: String
        get() = "https://$accountId.cloudflarestorage.com"

    private val credentials: AwsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey)

    private val serviceConfiguration: S3Configuration = S3Configuration.builder()
        .pathStyleAccessEnabled(true)
        .build()

    val s3Client: S3Client = S3Client.builder()
        .endpointOverride(URI.create(endpoint))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.of("auto"))
        .serviceConfiguration(serviceConfiguration)
        .build()

    val presigner: S3Presigner = S3Presigner.builder()
        .endpointOverride(URI.create(endpoint))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.of("auto"))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build())
        .build()

    fun generatePresignedUploadUrl(bucketName: String, objectKey: String, expiration: Duration): String {
        val presignRequest: PutObjectPresignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(expiration)
            .putObjectRequest { builder -> builder
                .bucket(bucketName)
                .key(objectKey)
                .contentType("application/pdf")
                .build()}
            .build()

        val presignedRequest = presigner.presignPutObject(presignRequest)
        return presignedRequest.url().toString()
    }

    fun generatePresignedReadUrl(bucketName: String, objectKey: String, expiration: Duration): String {
        val presignRequest: GetObjectPresignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(expiration)
            .getObjectRequest { builder -> builder
                .bucket(bucketName)
                .key(objectKey)
                .build()}
            .build()

        val presignedRequest = presigner.presignGetObject(presignRequest)
        return presignedRequest.url().toString()
    }

    fun deleteObject(bucketName: String, objectKey: String) {
        try {
            s3Client.deleteObject { builder ->
                builder.bucket(bucketName)
                    .key(objectKey)
                    .build()
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to delete object $objectKey from $bucketName", e)
        }
    }

    fun updateOrCreateObject(bucketName: String, objectKey: String, filePath: Path) {
        try {
            s3Client.putObject({ builder ->
                builder.bucket(bucketName)
                    .key(objectKey)
                    .contentType("application/pdf")
                    .build()
            }, RequestBody.fromFile(filePath))
        } catch (e: Exception) {
            throw RuntimeException("Failed to upload object $objectKey to $bucketName", e)
        }
    }

    fun downloadObject(bucketName: String, objectKey: String): Path {
        try {
            val tempFile = Files.createTempFile("note-", ".pdf")
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build()

            s3Client.getObject(getObjectRequest, ResponseTransformer.toFile(tempFile))
            return tempFile
        } catch (e: Exception) {
            throw RuntimeException("Failed to download object $objectKey from $bucketName", e)
        }
    }
}