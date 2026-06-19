package crm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Cloud-native S3 storage service for file operations.
 * Provides abstraction layer for Amazon S3 operations.
 */
@Service
@Slf4j
public class S3StorageService {

    @Autowired(required = false)
    private S3Client s3Client;

    @Value("${aws.s3.bucket.name:crm-data-bucket}")
    private String bucketName;

    /**
     * Uploads a file to S3.
     *
     * @param key The S3 object key
     * @param content The file content as byte array
     * @param contentType The content type (e.g., "application/pdf")
     * @throws IOException if upload fails
     */
    public void uploadFile(String key, byte[] content, String contentType) throws IOException {
        if (s3Client == null) {
            log.warn("S3Client not configured. File upload skipped: {}", key);
            return;
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));
            log.info("Successfully uploaded file to S3: s3://{}/{}", bucketName, key);
        } catch (S3Exception e) {
            log.error("Failed to upload file to S3: {}", key, e);
            throw new IOException("Failed to upload file to S3: " + key, e);
        }
    }

    /**
     * Downloads a file from S3.
     *
     * @param key The S3 object key
     * @return InputStream of the file content
     * @throws IOException if download fails
     */
    public InputStream downloadFile(String key) throws IOException {
        if (s3Client == null) {
            throw new IOException("S3Client not configured");
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            log.info("Successfully downloaded file from S3: s3://{}/{}", bucketName, key);
            return s3Object;
        } catch (S3Exception e) {
            log.error("Failed to download file from S3: {}", key, e);
            throw new IOException("Failed to download file from S3: " + key, e);
        }
    }

    /**
     * Checks if a file exists in S3.
     *
     * @param key The S3 object key
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists(String key) {
        if (s3Client == null) {
            return false;
        }

        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error checking file existence in S3: {}", key, e);
            return false;
        }
    }

    /**
     * Deletes a file from S3.
     *
     * @param key The S3 object key
     * @throws IOException if deletion fails
     */
    public void deleteFile(String key) throws IOException {
        if (s3Client == null) {
            log.warn("S3Client not configured. File deletion skipped: {}", key);
            return;
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file from S3: s3://{}/{}", bucketName, key);
        } catch (S3Exception e) {
            log.error("Failed to delete file from S3: {}", key, e);
            throw new IOException("Failed to delete file from S3: " + key, e);
        }
    }
}
