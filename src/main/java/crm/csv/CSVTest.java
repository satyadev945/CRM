package crm.csv;

import com.opencsv.CSVReader;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Cloud-native CSV processing using Amazon S3.
 * Replaces local file system dependencies with S3 object storage.
 */
public class CSVTest {

    /**
     * Read and process CSV file from Amazon S3.
     * 
     * @param s3Client AWS S3 client instance
     * @param bucketName S3 bucket name
     * @param s3Key S3 object key (path to CSV file)
     */
    public static void processCSVFromS3(S3Client s3Client, String bucketName, String s3Key) {
        CSVReader reader = null;
        List<Object[]> data = new ArrayList<>();
        
        try {
            // Retrieve CSV file from S3
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            
            // Read CSV from S3 stream
            reader = new CSVReader(new InputStreamReader(s3Object));
            String[] line;
            
            while ((line = reader.readNext()) != null) {
                data.add(line);
                if (line.length > 1 && line[1].equals("QUICK SUB")) {
                    System.out.println(line[0] + "\t" + line[1] + "\t" + (line.length > 2 ? line[2] : ""));
                }
            }
            
            System.out.println("Successfully processed " + data.size() + " rows from S3: s3://" + bucketName + "/" + s3Key);
            
        } catch (S3Exception e) {
            System.err.println("Failed to retrieve CSV from S3: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Failed to read CSV content: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Main method for testing CSV processing from S3.
     * In production, this would be called from a service or controller.
     * 
     * Environment variables required:
     * - AWS_S3_BUCKET_NAME: S3 bucket name
     * - AWS_S3_CSV_KEY: S3 object key for the CSV file
     */
    public static void main(String[] args) {
        // Configuration should come from environment variables or Spring configuration
        String bucketName = System.getenv("AWS_S3_BUCKET_NAME");
        String s3Key = System.getenv("AWS_S3_CSV_KEY");
        
        if (bucketName == null || s3Key == null) {
            System.err.println("ERROR: AWS_S3_BUCKET_NAME and AWS_S3_CSV_KEY environment variables must be set");
            System.err.println("Example: AWS_S3_BUCKET_NAME=my-bucket AWS_S3_CSV_KEY=data/file.csv");
            System.exit(1);
        }
        
        // Create S3 client (in production, this should be injected via Spring)
        S3Client s3Client = S3Client.builder().build();
        
        try {
            processCSVFromS3(s3Client, bucketName, s3Key);
        } finally {
            s3Client.close();
        }
    }

}
