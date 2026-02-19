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

/**
 * Cloud-native storage service using AWS S3
 * Replaces local file system operations for cloud deployment
 */
@Service
@Slf4j
public class S3StorageService {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.region}")
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
        
        log.info("S3StorageService initialized with bucket: {} in region: {}", bucketName, region);
    }

    /**
     * Upload PDF content to S3
     * @param fileName The name of the file
     * @param content The PDF content as byte array
     * @return The S3 object key
     */
    public String uploadPdf(String fileName, byte[] content) {
        try {
            if (!fileName.endsWith(".pdf")) {
                fileName += ".pdf";
            }
            
            String key = "pdfs/" + System.currentTimeMillis() + "_" + fileName;
            
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(content.length);
            metadata.setContentType("application/pdf");
            
            InputStream inputStream = new ByteArrayInputStream(content);
            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream, metadata);
            
            s3Client.putObject(request);
            
            log.info("Successfully uploaded PDF to S3: {}", key);
            return key;
        } catch (Exception e) {
            log.error("Failed to upload PDF to S3: {}", fileName, e);
            throw new RuntimeException("Failed to upload PDF to S3", e);
        }
    }

    /**
     * Get the public URL for an S3 object
     * @param key The S3 object key
     * @return The URL to access the object
     */
    public String getObjectUrl(String key) {
        return s3Client.getUrl(bucketName, key).toString();
    }

    /**
     * Check if S3 service is available
     * @return true if S3 is accessible
     */
    public boolean isAvailable() {
        try {
            s3Client.doesBucketExistV2(bucketName);
            return true;
        } catch (Exception e) {
            log.error("S3 service is not available", e);
            return false;
        }
    }
}
