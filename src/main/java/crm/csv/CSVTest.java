package crm.csv;

import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cloud-ready CSV processor.
 * Refactored to use InputStream instead of File for cloud compatibility.
 * In cloud environments, CSV files should be read from S3 or passed as multipart uploads.
 */
@Slf4j
public class CSVTest {

    /**
     * Process CSV from InputStream (cloud-compatible).
     * @param csvInputStream InputStream from S3, HTTP upload, or classpath resource
     * @return List of parsed CSV rows
     */
    public static List<Object[]> processCsv(InputStream csvInputStream) {
        List<Object[]> data = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvInputStream))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                data.add(line);
                if (line.length > 1 && "QUICK SUB".equals(line[1])) {
                    log.info("Found QUICK SUB record: {} {} {}",
                            line.length > 0 ? line[0] : "",
                            line[1],
                            line.length > 2 ? line[2] : "");
                }
            }
            log.info("Successfully processed {} CSV records", data.size());
        } catch (IOException e) {
            log.error("Failed to process CSV file", e);
        }

        return data;
    }

    /**
     * Example main method - In cloud environments, this would be triggered by:
     * - S3 event notification
     * - REST API endpoint with file upload
     * - Message queue event
     */
    public static void main(String[] args) {
        // Example: Load from classpath resource
        try (InputStream csvStream = CSVTest.class.getClassLoader().getResourceAsStream("sample.csv")) {
            if (csvStream != null) {
                List<Object[]> data = processCsv(csvStream);
                log.info("Loaded {} records from CSV", data.size());
            } else {
                log.warn("CSV file not found in classpath. In cloud environment, configure S3 bucket or file upload endpoint.");
            }
        } catch (IOException e) {
            log.error("Failed to load CSV resource", e);
        }

        // TODO: For cloud deployment, implement REST endpoint:
        // @PostMapping("/api/csv/upload")
        // public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file)
        // and process with: processCsv(file.getInputStream())
    }

}
