package crm.csv;

import com.opencsv.CSVReader;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV processing utility that reads CSV data from Amazon S3 instead of the local file system.
 * Replaces java.io.File-based operations with AWS SDK for Java v2 S3 client calls
 * to achieve cloud-native, durable, and scalable storage without host-level file system dependencies.
 */
public class CSVTest {

    public static void main(String[] args) {
        // Retrieve S3 configuration from environment variables
        String bucketName = System.getenv("AWS_S3_BUCKET_NAME");
        String s3Key = System.getenv("AWS_S3_CSV_KEY");

        if (bucketName == null || bucketName.isEmpty()) {
            System.err.println("Environment variable AWS_S3_BUCKET_NAME is not set.");
            return;
        }
        if (s3Key == null || s3Key.isEmpty()) {
            System.err.println("Environment variable AWS_S3_CSV_KEY is not set.");
            return;
        }

        // Build the S3 client using the default credential provider chain (IAM role / env vars)
        S3Client s3Client = S3Client.builder().build();

        // Retrieve the CSV file directly from Amazon S3 as an InputStream
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        List<Object[]> data = new ArrayList<>();
        try {
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            // Wrap the S3 InputStream in a CSVReader — no local File dependency
            CSVReader reader = new CSVReader(new InputStreamReader(s3Object, StandardCharsets.UTF_8));
            String[] line;
            while ((line = reader.readNext()) != null) {
                data.add(line);
                if (line.length > 1 && line[1].equals("QUICK SUB")) {
                    System.out.println(line[0] + "\t" + line[1] + "\t" + line[2]);
                }
            }
            reader.close();
            System.out.println("Successfully processed CSV from S3: bucket=" + bucketName + ", key=" + s3Key);
        } catch (IOException e) {
            System.err.println("Failed to read CSV from S3: " + e.getMessage());
            e.printStackTrace();
        } finally {
            s3Client.close();
        }
    }

}
