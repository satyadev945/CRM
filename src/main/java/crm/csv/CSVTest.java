package crm.csv;

import com.opencsv.CSVReader;
import crm.utils.ReadDataUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV Test utility - Updated for containerized environments.
 * File path should be provided via environment variable or command line argument.
 */
public class CSVTest {

    public static void main(String[] args) {
        // Get file path from environment variable or command line argument
        String csvFilePath = System.getenv("CSV_FILE_PATH");
        
        if (csvFilePath == null || csvFilePath.trim().isEmpty()) {
            if (args.length > 0) {
                csvFilePath = args[0];
            } else {
                System.err.println("Error: CSV file path not provided.");
                System.err.println("Please set CSV_FILE_PATH environment variable or pass file path as argument.");
                System.err.println("Example: java crm.csv.CSVTest /path/to/file.csv");
                System.exit(1);
                return;
            }
        }
        
        try {
            // Use container-friendly file reading method
            File document = ReadDataUtils.readFileFromPath(csvFilePath, "csv");
            System.out.println("Processing file: " + document.getName());

            CSVReader reader;
            List<Object[]> data = new ArrayList<>();
            try {
                reader = new CSVReader(new FileReader(document));
                String[] line;
                while ((line = reader.readNext()) != null) {
                    data.add(line);
                    if(line.length > 1 && line[1].equals("QUICK SUB")){
                        System.out.println(line[0] + "\t" + line[1] + "\t" + line[2]);
                    }
                }
                reader.close();
                System.out.println("Successfully processed " + data.size() + " rows from CSV file.");
            } catch (IOException e) {
                System.err.println("Error reading CSV file: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

}
