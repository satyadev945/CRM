package crm.utils;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Cloud-native utility for reading data from Amazon S3.
 * Replaces local file system dependencies with S3 object storage.
 */
public class ReadDataUtils {

    private final S3Client s3Client;
    private final String bucketName;

    /**
     * Constructor with S3 client and bucket name.
     * These should be injected via Spring configuration.
     * 
     * @param s3Client AWS S3 client instance
     * @param bucketName S3 bucket name from environment variable
     */
    public ReadDataUtils(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    /**
     * Read file content from S3 bucket.
     * 
     * @param s3Key The S3 object key (path within bucket)
     * @return InputStream of the file content
     * @throws S3Exception if the object cannot be retrieved
     * @throws IOException if there's an error reading the stream
     */
    public InputStream readFileFromS3(String s3Key) throws S3Exception, IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
        
        // Convert to ByteArrayInputStream to allow multiple reads
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = s3Object.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        s3Object.close();
        
        return new java.io.ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Read file content from S3 as byte array.
     * 
     * @param s3Key The S3 object key (path within bucket)
     * @return byte array of the file content
     * @throws S3Exception if the object cannot be retrieved
     * @throws IOException if there's an error reading the stream
     */
    public byte[] readFileAsBytesFromS3(String s3Key) throws S3Exception, IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = s3Object.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        s3Object.close();
        
        return baos.toByteArray();
    }
}
