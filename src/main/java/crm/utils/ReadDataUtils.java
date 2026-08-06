package crm.utils;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.InputStream;

public class ReadDataUtils {

    /**
     * Replaced JFileChooser with Amazon S3 object retrieval to ensure cloud readiness.
     * Instead of a local file dialog, this method now retrieves a file from an S3 bucket.
     * 
     * @param bucketName The name of the S3 bucket.
     * @param key The key (path) of the object in the S3 bucket.
     * @return An InputStream of the S3 object.
     * @throws Exception if retrieval fails.
     */
    public static InputStream readFileFromS3(String bucketName, String key) {
        S3Client s3 = S3Client.builder().build();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        return s3.getObject(getObjectRequest);
    }

}
