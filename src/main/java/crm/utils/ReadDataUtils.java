package crm.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for reading data files in a containerized environment.
 * Refactored from GUI-based (JFileChooser) to web-based file handling.
 */
public class ReadDataUtils {

    /**
     * Saves an uploaded file to a temporary location and returns the File object.
     * This method is designed for containerized environments and replaces the GUI-based file selection.
     * 
     * @param uploadedFile The MultipartFile uploaded through a web endpoint
     * @param fileExtension Expected file extension for validation (e.g., "csv", "xlsx")
     * @return File object pointing to the saved temporary file
     * @throws IOException if file operations fail
     * @throws IllegalArgumentException if file extension doesn't match
     */
    public static File processUploadedFile(MultipartFile uploadedFile, String fileExtension) throws IOException {
        if (uploadedFile == null || uploadedFile.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or null");
        }
        
        String originalFilename = uploadedFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith("." + fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("File must have ." + fileExtension + " extension");
        }
        
        // Create temporary file
        Path tempDir = Files.createTempDirectory("crm-uploads");
        Path tempFile = tempDir.resolve(originalFilename);
        
        // Save uploaded file to temporary location
        Files.copy(uploadedFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        
        return tempFile.toFile();
    }
    
    /**
     * Reads a file from a specified path. Useful for reading files from mounted volumes
     * or environment-configured paths in containerized deployments.
     * 
     * @param filePath The path to the file (can be from environment variable)
     * @param fileExtension Expected file extension for validation
     * @return File object
     * @throws IllegalArgumentException if file doesn't exist or extension doesn't match
     */
    public static File readFileFromPath(String filePath, String fileExtension) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        
        if (!file.isFile()) {
            throw new IllegalArgumentException("Path is not a file: " + filePath);
        }
        
        if (fileExtension != null && !file.getName().toLowerCase().endsWith("." + fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("File must have ." + fileExtension + " extension");
        }
        
        return file;
    }
    
    /**
     * Legacy method signature maintained for backward compatibility.
     * This method is deprecated and should not be used in containerized environments.
     * 
     * @deprecated Use processUploadedFile() or readFileFromPath() instead.
     *             GUI-based file selection is not compatible with containerized deployments.
     */
    @Deprecated
    public static File ReadFile(String dialogMessage, Object parent, String fileExtensionDescription,
                                String... fileExtension) {
        throw new UnsupportedOperationException(
            "GUI-based file selection is not supported in containerized environments. " +
            "Use processUploadedFile() for web uploads or readFileFromPath() for file system access."
        );
    }

}
