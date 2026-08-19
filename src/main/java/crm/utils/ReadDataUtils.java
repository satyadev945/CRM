package crm.utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Utility class for reading data files from Azure Blob Storage.
 * Replaces the previous Swing-based JFileChooser (desktop GUI) implementation
 * with a cloud-native Azure Blob Storage approach suitable for server-side
 * and cloud-deployed environments.
 */
public class ReadDataUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReadDataUtils.class);

    /**
     * Downloads a blob (file) from Azure Blob Storage and returns its content
     * as an InputStream.
     *
     * <p>Connection string and container name are read from environment variables:
     * <ul>
     *   <li>{@code AZURE_STORAGE_CONNECTION_STRING} – Azure Storage account connection string</li>
     *   <li>{@code AZURE_STORAGE_CONTAINER_NAME}    – Blob container name (defaults to "crm-files")</li>
     * </ul>
     *
     * @param blobName                  the name of the blob (file) to download
     * @param fileExtensionDescription  human-readable description of the expected file type (used for logging)
     * @param fileExtensions            accepted file extensions (used for validation)
     * @return an {@link InputStream} with the blob content, or {@code null} if the blob
     *         does not exist or an error occurs
     */
    public static InputStream readFile(String blobName,
                                       String fileExtensionDescription,
                                       String... fileExtensions) {
        if (blobName == null || blobName.trim().isEmpty()) {
            logger.warn("Blob name must not be null or empty.");
            return null;
        }

        // Validate file extension if extensions are specified
        if (fileExtensions != null && fileExtensions.length > 0) {
            boolean extensionMatched = false;
            String lowerBlobName = blobName.toLowerCase();
            for (String ext : fileExtensions) {
                if (lowerBlobName.endsWith("." + ext.toLowerCase())) {
                    extensionMatched = true;
                    break;
                }
            }
            if (!extensionMatched) {
                logger.warn("Blob '{}' does not match expected file type(s): {} ({})",
                        blobName, fileExtensionDescription, String.join(", ", fileExtensions));
                return null;
            }
        }

        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        if (connectionString == null || connectionString.trim().isEmpty()) {
            logger.error("Environment variable AZURE_STORAGE_CONNECTION_STRING is not set.");
            return null;
        }

        String containerName = System.getenv("AZURE_STORAGE_CONTAINER_NAME");
        if (containerName == null || containerName.trim().isEmpty()) {
            containerName = "crm-files";
            logger.info("AZURE_STORAGE_CONTAINER_NAME not set; using default container: {}", containerName);
        }

        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

            if (!containerClient.exists()) {
                logger.error("Azure Blob Storage container '{}' does not exist.", containerName);
                return null;
            }

            BlobClient blobClient = containerClient.getBlobClient(blobName);

            if (!blobClient.exists()) {
                logger.warn("Blob '{}' not found in container '{}'.", blobName, containerName);
                return null;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blobClient.downloadStream(outputStream);
            logger.info("Successfully downloaded blob '{}' from container '{}'.", blobName, containerName);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (Exception e) {
            logger.error("Failed to read blob '{}' from Azure Blob Storage: {}", blobName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Uploads a file (provided as a byte array) to Azure Blob Storage.
     *
     * @param blobName    the target blob name (path within the container)
     * @param data        the file content as a byte array
     * @param overwrite   whether to overwrite an existing blob with the same name
     * @return {@code true} if the upload succeeded, {@code false} otherwise
     */
    public static boolean uploadFile(String blobName, byte[] data, boolean overwrite) {
        if (blobName == null || blobName.trim().isEmpty()) {
            logger.warn("Blob name must not be null or empty.");
            return false;
        }

        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        if (connectionString == null || connectionString.trim().isEmpty()) {
            logger.error("Environment variable AZURE_STORAGE_CONNECTION_STRING is not set.");
            return false;
        }

        String containerName = System.getenv("AZURE_STORAGE_CONTAINER_NAME");
        if (containerName == null || containerName.trim().isEmpty()) {
            containerName = "crm-files";
        }

        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            containerClient.createIfNotExists();

            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.upload(new ByteArrayInputStream(data), data.length, overwrite);
            logger.info("Successfully uploaded blob '{}' to container '{}'.", blobName, containerName);
            return true;

        } catch (Exception e) {
            logger.error("Failed to upload blob '{}' to Azure Blob Storage: {}", blobName, e.getMessage(), e);
            return false;
        }
    }
}
