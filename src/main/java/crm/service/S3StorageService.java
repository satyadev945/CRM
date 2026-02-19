package crm.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * Cloud-native storage service using AWS S3
 * Replaces local file system operations with cloud storage
 */
@Service
@Slf4j
public class S3StorageService {

    @Value("${cloud.aws.s3.bucket:crm-documents}")
    private String bucketName;

    @Value("${cloud.aws.region.static:us-east-1}")
    private String region;

    private AmazonS3 s3Client;

    @PostConstruct
    public void init() {
        // Use DefaultAWSCredentialsProviderChain for cloud-native credential management
        // This supports IAM roles, environment variables, and AWS credentials file
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
        
        log.info("S3StorageService initialized with bucket: {}, region: {}", bucketName, region);
    }

    /**
     * Upload PDF to S3 bucket
     * @param fileName Original file name
     * @param pdfBytes PDF content as byte array
     * @return S3 key (path) of uploaded file
     */
    public String uploadPdf(String fileName, byte[] pdfBytes) {
        String s3Key = "pdfs/" + UUID.randomUUID().toString() + "/" + fileName;
        
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(pdfBytes.length);
            metadata.setContentType("application/pdf");
            
            InputStream inputStream = new ByteArrayInputStream(pdfBytes);
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, s3Key, inputStream, metadata);
            
            s3Client.putObject(putRequest);
            log.info("Successfully uploaded PDF to S3. bucket: {}, key: {}", bucketName, s3Key);
            
            return s3Key;
        } catch (Exception e) {
            log.error("Failed to upload PDF to S3. bucket: {}, key: {}, error: {}", bucketName, s3Key, e.getMessage(), e);
            throw new RuntimeException("Failed to upload PDF to S3", e);
        }
    }

    /**
     * Upload CSV to S3 bucket
     * @param fileName Original file name
     * @param csvBytes CSV content as byte array
     * @return S3 key (path) of uploaded file
     */
    public String uploadCsv(String fileName, byte[] csvBytes) {
        String s3Key = "csv/" + UUID.randomUUID().toString() + "/" + fileName;
        
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(csvBytes.length);
            metadata.setContentType("text/csv");
            
            InputStream inputStream = new ByteArrayInputStream(csvBytes);
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, s3Key, inputStream, metadata);
            
            s3Client.putObject(putRequest);
            log.info("Successfully uploaded CSV to S3. bucket: {}, key: {}", bucketName, s3Key);
            
            return s3Key;
        } catch (Exception e) {
            log.error("Failed to upload CSV to S3. bucket: {}, key: {}, error: {}", bucketName, s3Key, e.getMessage(), e);
            throw new RuntimeException("Failed to upload CSV to S3", e);
        }
    }

    /**
     * Download file from S3
     * @param s3Key S3 key (path) of the file
     * @return File content as byte array
     */
    public byte[] downloadFile(String s3Key) {
        try {
            InputStream inputStream = s3Client.getObject(bucketName, s3Key).getObjectContent();
            byte[] content = inputStream.readAllBytes();
            inputStream.close();
            
            log.info("Successfully downloaded file from S3. bucket: {}, key: {}", bucketName, s3Key);
            return content;
        } catch (Exception e) {
            log.error("Failed to download file from S3. bucket: {}, key: {}, error: {}", bucketName, s3Key, e.getMessage(), e);
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }

    /**
     * Delete file from S3
     * @param s3Key S3 key (path) of the file
     */
    public void deleteFile(String s3Key) {
        try {
            s3Client.deleteObject(bucketName, s3Key);
            log.info("Successfully deleted file from S3. bucket: {}, key: {}", bucketName, s3Key);
        } catch (Exception e) {
            log.error("Failed to delete file from S3. bucket: {}, key: {}, error: {}", bucketName, s3Key, e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }
}
