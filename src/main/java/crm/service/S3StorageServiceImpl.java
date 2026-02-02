package crm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.utils.IoUtils;

/**
 * AWS S3 implementation of the StorageService interface.
 * Used in production and cloud environments.
 */
@Service
@Profile({"prod", "cloud", "aws"})
@Slf4j
public class S3StorageServiceImpl implements StorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageServiceImpl(@Value("${aws.region}") String region,
                               @Value("${aws.s3.bucket}") String bucketName) {
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .build();

        // Ensure the bucket exists
        try {
            if (!bucketExists(bucketName)) {
                createBucket(bucketName);
            }
        } catch (SdkException e) {
            log.error("Error initializing S3 bucket: {}", e.getMessage(), e);
        }
    }

    private boolean bucketExists(String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

    private void createBucket(String bucketName) {
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(bucketName)
                .build());
    }

    @Override
    public String storeFile(String fileName, byte[] content) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(content));
            log.info("File '{}' uploaded to S3 bucket '{}'", fileName, bucketName);
            return getFileUrl(fileName);
        } catch (SdkException e) {
            log.error("Error storing file in S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store file: " + fileName, e);
        }
    }

    @Override
    public String storeFile(String fileName, InputStream inputStream) {
        try {
            byte[] bytes = IoUtils.toByteArray(inputStream);
            return storeFile(fileName, bytes);
        } catch (Exception e) {
            log.error("Error storing file from input stream: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store file from input stream: " + fileName, e);
        }
    }

    @Override
    public byte[] getFile(String fileName) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getRequest);
            return IoUtils.toByteArray(response);
        } catch (NoSuchKeyException e) {
            log.error("File '{}' not found in S3 bucket '{}'", fileName, bucketName);
            throw new RuntimeException("File not found: " + fileName, e);
        } catch (Exception e) {
            log.error("Error retrieving file from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve file: " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("File '{}' deleted from S3 bucket '{}'", fileName, bucketName);
        } catch (SdkException e) {
            log.error("Error deleting file from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file: " + fileName, e);
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.headObject(headRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (SdkException e) {
            log.error("Error checking if file exists in S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check if file exists: " + fileName, e);
        }
    }

    private String getFileUrl(String fileName) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }
}