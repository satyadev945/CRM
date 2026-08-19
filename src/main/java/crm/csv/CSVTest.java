package crm.csv;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSVTest {

    public static void main(String[] args) {
        // Azure Storage connection string should be retrieved from environment variables for cloud readiness
        String connectStr = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        String containerName = System.getenv("AZURE_STORAGE_CONTAINER_NAME");
        String blobName = System.getenv("AZURE_STORAGE_BLOB_NAME");

        if (connectStr == null || containerName == null || blobName == null) {
            System.err.println("Missing Azure Storage environment variables: AZURE_STORAGE_CONNECTION_STRING, AZURE_STORAGE_CONTAINER_NAME, or AZURE_STORAGE_BLOB_NAME");
            return;
        }

        try {
            BlobContainerClient containerClient = new BlobServiceClientBuilder()
                    .connectionString(connectStr)
                    .buildClient()
                    .getBlobContainerClient(containerName);

            BlobClient blobClient = containerClient.getBlobClient(blobName);

            CSVReader reader;
            List<Object[]> data = new ArrayList<>();
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(blobClient.openInputStream()))) {
                reader = new CSVReader(br);
                String[] line;
                while ((line = reader.readNext()) != null) {
                    data.add(line);
                    if (line.length > 1 && "QUICK SUB".equals(line[1])) {
                        System.out.println(line[0] + "\t" + line[1] + "\t" + (line.length > 2 ? line[2] : ""));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
