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
 * Cloud-ready CSV processing utility that reads CSV files from Amazon S3
 * instead of local file system, eliminating host-level file system dependencies.
 */
public class CSVTest {

    private static final String S3_BUCKET_NAME = System.getenv().getOrDefault("S3_BUCKET_NAME", "crm-data-bucket");
    private static final String DEFAULT_CSV_KEY = System.getenv().getOrDefault("CSV_S3_KEY", "data/sample.csv");

    /**
     * Reads and processes CSV data from Amazon S3.
     * 
     * @param s3Key The S3 object key for the CSV file
     * @return List of parsed CSV rows
     */
    public static List<Object[]> readCsvFromS3(String s3Key) {
        List<Object[]> data = new ArrayList<>();

        try (S3Client s3Client = S3Client.builder().build()) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(S3_BUCKET_NAME)
                    .key(s3Key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            
            // Read CSV directly from S3 stream without writing to local file system
            try (CSVReader reader = new CSVReader(new InputStreamReader(s3Object))) {
                String[] line;
                while ((line = reader.readNext()) != null) {
                    data.add(line);
                    // Process specific records
                    if (line.length > 1 && "QUICK SUB".equals(line[1])) {
                        System.out.println(line[0] + "\t" + line[1] + "\t" + (line.length > 2 ? line[2] : ""));
                    }
                }
                System.out.println("Successfully processed " + data.size() + " rows from S3: " + s3Key);
            }

        } catch (S3Exception e) {
            System.err.println("S3 error while reading CSV: " + e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            System.err.println("IO error while processing CSV from S3: " + e.getMessage());
        }

        return data;
    }

    /**
     * Main method for testing CSV processing from S3.
     * Uses environment variable CSV_S3_KEY or default value.
     */
    public static void main(String[] args) {
        String s3Key = args.length > 0 ? args[0] : DEFAULT_CSV_KEY;
        
        System.out.println("Reading CSV from S3: s3://" + S3_BUCKET_NAME + "/" + s3Key);
        List<Object[]> data = readCsvFromS3(s3Key);
        
        if (!data.isEmpty()) {
            System.out.println("Total rows processed: " + data.size());
            // Example: Print first two rows if available
            if (data.size() > 0) {
                Object[] firstRow = data.get(0);
                if (firstRow.length > 2) {
                    System.out.println("First row: " + firstRow[1] + "\t" + firstRow[2]);
                }
            }
            if (data.size() > 1) {
                Object[] secondRow = data.get(1);
                if (secondRow.length > 2) {
                    System.out.println("Second row: " + secondRow[1] + "\t" + secondRow[2]);
                }
            }
        } else {
            System.out.println("No data found or error occurred");
        }
    }

}
