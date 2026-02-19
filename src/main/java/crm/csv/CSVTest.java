package crm.csv;

import com.opencsv.CSVReader;
import crm.utils.ReadDataUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cloud-ready CSV processing utility
 * Removed desktop GUI dependencies and local file system operations
 * Uses classpath resources or cloud storage instead
 */
@Slf4j
public class CSVTest {

    /**
     * Process CSV from classpath resource
     * @param resourcePath Path to CSV in classpath (e.g., "data/sample.csv")
     * @return List of CSV rows
     */
    public static List<String[]> processCsvFromClasspath(String resourcePath) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        
        log.info("Processing CSV from classpath. correlationId: {}, resourcePath: {}", correlationId, resourcePath);
        
        List<String[]> data = new ArrayList<>();
        CSVReader reader = null;
        
        try {
            InputStream inputStream = ReadDataUtils.readFromClasspath(resourcePath);
            reader = new CSVReader(new InputStreamReader(inputStream));
            
            String[] line;
            int rowCount = 0;
            while ((line = reader.readNext()) != null) {
                data.add(line);
                rowCount++;
                
                // Log specific rows for debugging (cloud-compatible structured logging)
                if (line.length > 1 && "QUICK SUB".equals(line[1])) {
                    log.info("Found matching row. correlationId: {}, row: {}, col0: {}, col1: {}, col2: {}", 
                            correlationId, rowCount, 
                            line.length > 0 ? line[0] : "", 
                            line.length > 1 ? line[1] : "", 
                            line.length > 2 ? line[2] : "");
                }
            }
            
            log.info("CSV processing completed. correlationId: {}, totalRows: {}", correlationId, rowCount);
            
        } catch (IOException e) {
            log.error("Error processing CSV. correlationId: {}, error: {}", correlationId, e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Error closing CSV reader. correlationId: {}, error: {}", correlationId, e.getMessage(), e);
                }
            }
            MDC.remove("correlationId");
        }
        
        return data;
    }

    /**
     * Process CSV from input stream (for cloud storage integration)
     * @param inputStream CSV input stream
     * @param fileName File name for logging
     * @return List of CSV rows
     */
    public static List<String[]> processCsvFromStream(InputStream inputStream, String fileName) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        
        log.info("Processing CSV from stream. correlationId: {}, fileName: {}", correlationId, fileName);
        
        List<String[]> data = new ArrayList<>();
        CSVReader reader = null;
        
        try {
            reader = new CSVReader(new InputStreamReader(inputStream));
            
            String[] line;
            int rowCount = 0;
            while ((line = reader.readNext()) != null) {
                data.add(line);
                rowCount++;
            }
            
            log.info("CSV processing completed. correlationId: {}, fileName: {}, totalRows: {}", 
                    correlationId, fileName, rowCount);
            
        } catch (IOException e) {
            log.error("Error processing CSV. correlationId: {}, fileName: {}, error: {}", 
                    correlationId, fileName, e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Error closing CSV reader. correlationId: {}, error: {}", correlationId, e.getMessage(), e);
                }
            }
            MDC.remove("correlationId");
        }
        
        return data;
    }

    /**
     * Main method for testing - uses classpath resource instead of file chooser
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Example: Process CSV from classpath
        // In production, this would be triggered by a REST endpoint or message queue
        String resourcePath = "data/sample.csv"; // Place CSV in src/main/resources/data/
        
        log.info("Starting CSV processing application");
        List<String[]> data = processCsvFromClasspath(resourcePath);
        log.info("CSV processing completed. Total rows: {}", data.size());
    }
}
