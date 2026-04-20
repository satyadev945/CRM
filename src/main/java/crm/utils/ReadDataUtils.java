package crm.utils;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Cloud-ready utility for reading data from Amazon S3 instead of local file system.
 * This eliminates hard-coded file path dependencies and makes the application cloud-native.
 */
public class ReadDataUtils {

    private static final String S3_BUCKET_NAME = System.getenv().getOrDefault("S3_BUCKET_NAME", "crm-data-bucket");
    private static final String S3_REGION = System.getenv().getOrDefault("AWS_REGION", "us-east-1");

    /**
     * Reads a file from Amazon S3 and returns it as a temporary File object.
     * 
     * @param s3Key The S3 object key (path within the bucket)
     * @param fileExtension Expected file extension for validation
     * @return File object containing the downloaded S3 content, or null if error occurs
     */
    public static File readFileFromS3(String s3Key, String fileExtension) {
        if (s3Key == null || s3Key.isEmpty()) {
            System.err.println("S3 key cannot be null or empty");
            return null;
        }

        // Validate file extension
        if (fileExtension != null && !s3Key.endsWith("." + fileExtension)) {
            System.err.println("File does not have expected extension: " + fileExtension);
            return null;
        }

        try (S3Client s3Client = S3Client.builder().build()) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(S3_BUCKET_NAME)
                    .key(s3Key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            
            // Create temporary file to store S3 content
            String fileName = s3Key.substring(s3Key.lastIndexOf('/') + 1);
            File tempFile = File.createTempFile("s3-download-", "-" + fileName);
            tempFile.deleteOnExit();

            // Write S3 content to temporary file
            try (FileOutputStream fos = new FileOutputStream(tempFile);
                 InputStream is = s3Object) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Successfully downloaded file from S3: " + s3Key);
            return tempFile;

        } catch (S3Exception e) {
            System.err.println("S3 error while reading file: " + e.awsErrorDetails().errorMessage());
            return null;
        } catch (IOException e) {
            System.err.println("IO error while processing S3 file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Legacy method maintained for backward compatibility.
     * Deprecated - use readFileFromS3 instead for cloud-native operations.
     * 
     * @deprecated This method uses local file system which is not cloud-compatible.
     *             Use readFileFromS3(String s3Key, String fileExtension) instead.
     */
    @Deprecated
    public static File ReadFile(String dialogMessage, Object parent, String fileExtensionDescription,
                                String... fileExtension) {
        System.err.println("WARNING: ReadFile method is deprecated and not cloud-compatible. " +
                          "Use readFileFromS3 for cloud deployments.");
        // Return null to prevent usage in cloud environments
        return null;
    }

}
