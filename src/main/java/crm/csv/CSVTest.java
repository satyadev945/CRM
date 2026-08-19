package crm.csv;

import com.opencsv.CSVReader;
import crm.utils.ReadDataUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVTest demonstrates reading a CSV file from Azure Blob Storage.
 *
 * <p>The local {@code java.io.File} / {@code FileReader} approach has been replaced
 * with an Azure Blob Storage download via {@link ReadDataUtils#readFile}, which
 * returns an {@link InputStream} that is then wrapped in an {@link InputStreamReader}
 * for OpenCSV consumption.
 *
 * <p>Required environment variables (consumed by {@link ReadDataUtils}):
 * <ul>
 *   <li>{@code AZURE_STORAGE_CONNECTION_STRING} – Azure Storage account connection string</li>
 *   <li>{@code AZURE_STORAGE_CONTAINER_NAME}    – Blob container name (defaults to "crm-files")</li>
 * </ul>
 *
 * <p>The blob name (CSV file name) should be passed as the first command-line argument,
 * e.g.: {@code java -cp ... crm.csv.CSVTest mydata.csv}
 */
public class CSVTest {

    public static void main(String[] args) {
        // Resolve the blob name: prefer command-line argument, fall back to env var,
        // then a sensible default so the class remains runnable in all environments.
        String blobName = (args != null && args.length > 0 && args[0] != null && !args[0].trim().isEmpty())
                ? args[0].trim()
                : System.getenv().getOrDefault("CSV_BLOB_NAME", "data.csv");

        // Download the CSV blob from Azure Blob Storage as an InputStream.
        // ReadDataUtils.readFile validates the extension and handles all Azure SDK calls.
        InputStream blobStream = ReadDataUtils.readFile(blobName, "Only CSV Files", "csv");

        if (blobStream == null) {
            System.err.println("Could not retrieve blob '" + blobName + "' from Azure Blob Storage. "
                    + "Ensure AZURE_STORAGE_CONNECTION_STRING and AZURE_STORAGE_CONTAINER_NAME are set "
                    + "and the blob exists in the container.");
            return;
        }

        CSVReader reader;
        List<Object[]> data = new ArrayList<>();
        try {
            // Use InputStreamReader instead of FileReader to consume the cloud-sourced stream.
            reader = new CSVReader(new InputStreamReader(blobStream));
            String[] line;
            while ((line = reader.readNext()) != null) {
//                System.out.println(line[1] + "\t" + line[2]);
                data.add(line);
                if (line[1].equals("QUICK SUB")) {
                    System.out.println(line[0] + "\t" + line[1] + "\t" + line[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*System.out.println(data.get(0)[1] + "\t" + data.get(0)[2]);
        System.out.println(data.get(1)[1] + "\t" + data.get(1)[2]);*/
    }

}
