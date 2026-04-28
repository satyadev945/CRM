package crm.csv;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Cloud-native CSV processing using Amazon S3.
 * Replaces local file system dependencies with S3 object storage.
 */
@Component
public class CSVTest {

    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket.name:default-bucket}")
    private String bucketName;

    public CSVTest(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Reads and processes CSV file from Amazon S3.
     * 
     * @param s3Key The S3 object key for the CSV file
     * @return List of parsed CSV rows
     */
    public List<Object[]> processCSVFromS3(String s3Key) {
        List<Object[]> data = new ArrayList<>();
        
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            CSVReader reader = new CSVReader(new InputStreamReader(s3Object));
            
            String[] line;
            while ((line = reader.readNext()) != null) {
                data.add(line);
                if (line.length > 1 && line[1].equals("QUICK SUB")) {
                    System.out.println(line[0] + "\t" + line[1] + "\t" + (line.length > 2 ? line[2] : ""));
                }
            }
            reader.close();
            s3Object.close();
        } catch (IOException e) {
            System.err.println("Error reading CSV from S3: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data;
    }

    /**
     * Reads and processes CSV file from S3 with custom bucket.
     * 
     * @param bucketName The S3 bucket name
     * @param s3Key The S3 object key
     * @return List of parsed CSV rows
     */
    public List<Object[]> processCSVFromS3(String bucketName, String s3Key) {
        List<Object[]> data = new ArrayList<>();
        
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            CSVReader reader = new CSVReader(new InputStreamReader(s3Object));
            
            String[] line;
            while ((line = reader.readNext()) != null) {
                data.add(line);
                if (line.length > 1 && line[1].equals("QUICK SUB")) {
                    System.out.println(line[0] + "\t" + line[1] + "\t" + (line.length > 2 ? line[2] : ""));
                }
            }
            reader.close();
            s3Object.close();
        } catch (IOException e) {
            System.err.println("Error reading CSV from S3: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data;
    }

    /**
     * Example usage - can be called from a service or controller.
     * Removed main method as this should be a Spring-managed component.
     */
    public void exampleUsage() {
        // Example: processCSVFromS3("csv-files/sample.csv");
        System.out.println("CSVTest is now a Spring component. Use processCSVFromS3() method.");
    }
}
