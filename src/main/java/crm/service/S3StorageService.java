package crm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Cloud-native S3 service for file storage operations.
 * Provides centralized S3 operations for the application.
 */
@Service
@Slf4j
public class S3StorageService {

    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket.name:crm-pdf-bucket}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Upload file to S3.
     * 
     * @param key S3 object key
     * @param data File content as byte array
     * @param contentType MIME type of the file
     * @return S3 object key
     * @throws S3Exception if upload fails
     */
    public String uploadFile(String key, byte[] data, String contentType) throws S3Exception {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength((long) data.length)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
        log.info("File uploaded successfully to S3: s3://{}/{}", bucketName, key);
        return key;
    }

    /**
     * Download file from S3.
     * 
     * @param key S3 object key
     * @return File content as byte array
     * @throws S3Exception if download fails
     * @throws IOException if reading stream fails
     */
    public byte[] downloadFile(String key) throws S3Exception, IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = s3Object.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        s3Object.close();
        
        log.info("File downloaded successfully from S3: s3://{}/{}", bucketName, key);
        return baos.toByteArray();
    }

    /**
     * Get input stream for S3 object.
     * 
     * @param key S3 object key
     * @return InputStream of the file
     * @throws S3Exception if retrieval fails
     */
    public InputStream getFileStream(String key) throws S3Exception {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    /**
     * Delete file from S3.
     * 
     * @param key S3 object key
     * @throws S3Exception if deletion fails
     */
    public void deleteFile(String key) throws S3Exception {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("File deleted successfully from S3: s3://{}/{}", bucketName, key);
    }

    /**
     * Check if file exists in S3.
     * 
     * @param key S3 object key
     * @return true if file exists, false otherwise
     */
    public boolean fileExists(String key) {
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
            log.error("Error checking file existence in S3: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get pre-signed URL for temporary access to S3 object.
     * Useful for providing temporary download links.
     * 
     * @param key S3 object key
     * @param expirationMinutes URL expiration time in minutes
     * @return Pre-signed URL as string
     */
    public String getPresignedUrl(String key, int expirationMinutes) {
        // Note: Pre-signed URL generation requires additional AWS SDK dependency
        // This is a placeholder for the implementation
        log.info("Generating pre-signed URL for s3://{}/{} with expiration {} minutes", 
                bucketName, key, expirationMinutes);
        return String.format("s3://%s/%s", bucketName, key);
    }
}
