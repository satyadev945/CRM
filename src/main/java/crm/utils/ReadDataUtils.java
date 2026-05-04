package crm.utils;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for reading data from Amazon S3 instead of local file system.
 * Replaces hard-coded file path dependencies with cloud-native S3 object storage
 * using AWS SDK for Java v2.
 */
public class ReadDataUtils {

    /**
     * Retrieves an InputStream for the specified S3 object key from the configured bucket.
     * Replaces the local JFileChooser-based file selection with S3 object retrieval.
     *
     * @param s3Client   the AWS S3 client
     * @param bucketName the S3 bucket name (from environment variable AWS_S3_BUCKET_NAME)
     * @param s3Key      the S3 object key (path within the bucket)
     * @return InputStream of the S3 object content, or null if not found
     */
    public static InputStream readFileFromS3(S3Client s3Client, String bucketName, String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            System.out.println("Successfully retrieved S3 object: " + s3Key + " from bucket: " + bucketName);
            return s3Object;
        } catch (Exception e) {
            System.err.println("Failed to retrieve S3 object: " + s3Key + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Lists S3 objects in the specified bucket filtered by file extension.
     * Replaces the local JFileChooser file filter with S3 object listing.
     *
     * @param s3Client        the AWS S3 client
     * @param bucketName      the S3 bucket name
     * @param prefix          optional prefix/folder path within the bucket
     * @param fileExtension   file extension to filter (e.g., "csv", "pdf")
     * @return list of S3 object keys matching the extension
     */
    public static List<String> listFilesFromS3(S3Client s3Client, String bucketName, String prefix,
                                               String fileExtension) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix != null ? prefix : "")
                .build();
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        return listResponse.contents().stream()
                .map(S3Object::key)
                .filter(key -> key.toLowerCase().endsWith("." + fileExtension.toLowerCase()))
                .collect(Collectors.toList());
    }

}
