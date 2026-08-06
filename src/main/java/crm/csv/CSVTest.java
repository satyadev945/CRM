package crm.csv;

import com.opencsv.CSVReader;
import crm.utils.ReadDataUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVTest {

    public static void main(String[] args) {
        // Replaced local File usage with S3 retrieval to ensure cloud readiness.
        // In a real scenario, bucketName and key would be provided via configuration or environment variables.
        String bucketName = System.getenv("S3_BUCKET_NAME");
        String key = System.getenv("S3_FILE_KEY");

        if (bucketName == null || key == null) {
            System.err.println("S3_BUCKET_NAME and S3_FILE_KEY environment variables must be set.");
            return;
        }

        try (InputStream inputStream = ReadDataUtils.readFileFromS3(bucketName, key);
             BufferedReader readerWriter = new BufferedReader(new InputStreamReader(inputStream))) {
            
            CSVReader reader = new CSVReader(readerWriter);
            List<Object[]> data = new ArrayList<>();
            String[] line;
            while ((line = reader.readNext()) != null) {
                data.add(line);
                if(line.length > 1 && "QUICK SUB".equals(line[1])){
                    System.out.println(line[0] + "\t" + line[1] + "\t" + (line.length > 2 ? line[2] : ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
