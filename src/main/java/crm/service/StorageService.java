package crm.service;

import java.io.InputStream;

/**
 * Service interface for handling file storage operations.
 * Provides cloud-compatible storage functionality.
 */
public interface StorageService {

    /**
     * Store a file with the given filename and content
     *
     * @param fileName The name to save the file as
     * @param content The content to write to the file
     * @return The URL or path of the stored file
     */
    String storeFile(String fileName, byte[] content);

    /**
     * Store a file with the given filename from an input stream
     *
     * @param fileName The name to save the file as
     * @param inputStream The input stream to read content from
     * @return The URL or path of the stored file
     */
    String storeFile(String fileName, InputStream inputStream);

    /**
     * Retrieve a file by its name
     *
     * @param fileName The name of the file to retrieve
     * @return The file content as a byte array
     */
    byte[] getFile(String fileName);

    /**
     * Delete a file by its name
     *
     * @param fileName The name of the file to delete
     */
    void deleteFile(String fileName);

    /**
     * Check if a file exists
     *
     * @param fileName The name of the file to check
     * @return True if the file exists, false otherwise
     */
    boolean fileExists(String fileName);
}