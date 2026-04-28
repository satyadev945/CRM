package crm.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Cloud-native utility for reading data from Amazon S3.
 * Replaces local file system dependencies with S3 object storage.
 */
@Component
public class ReadDataUtils {

    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket.name:default-bucket}")
    private String bucketName;

    public ReadDataUtils(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Reads a file from Amazon S3 bucket.
     * 
     * @param s3Key The S3 object key (path within the bucket)
     * @return InputStream of the S3 object content
     */
    public InputStream readFileFromS3(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            return s3Object;
        } catch (Exception e) {
            System.err.println("Error reading file from S3: " + e.getMessage());
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    /**
     * Reads a file from S3 with custom bucket name.
     * 
     * @param bucketName The S3 bucket name
     * @param s3Key The S3 object key
     * @return InputStream of the S3 object content
     */
    public InputStream readFileFromS3(String bucketName, String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            return s3Object;
        } catch (Exception e) {
            System.err.println("Error reading file from S3: " + e.getMessage());
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}
