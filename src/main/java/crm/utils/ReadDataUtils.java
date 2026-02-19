package crm.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Cloud-ready utility for reading data files
 * Removed desktop GUI dependencies (JFileChooser) for cloud compatibility
 * Uses classpath resources or cloud storage instead
 */
@Slf4j
public class ReadDataUtils {

    /**
     * Read file from classpath resources (cloud-compatible)
     * @param resourcePath Path to resource in classpath (e.g., "data/sample.csv")
     * @return InputStream of the resource
     * @throws IOException if resource not found
     */
    public static InputStream readFromClasspath(String resourcePath) throws IOException {
        log.info("Reading file from classpath: {}", resourcePath);
        Resource resource = new ClassPathResource(resourcePath);
        if (!resource.exists()) {
            log.error("Resource not found in classpath: {}", resourcePath);
            throw new IOException("Resource not found: " + resourcePath);
        }
        return resource.getInputStream();
    }

    /**
     * Read file from specified path (for backward compatibility)
     * In cloud environments, this should be replaced with S3 or other cloud storage
     * @param filePath Absolute file path
     * @return File object
     * @deprecated Use readFromClasspath or cloud storage service instead
     */
    @Deprecated
    public static File readFile(String filePath) {
        log.warn("Using deprecated file system access. Consider using cloud storage. filePath: {}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("File not found: {}", filePath);
            return null;
        }
        return file;
    }

    /**
     * Validate file extension
     * @param fileName File name to validate
     * @param allowedExtensions Allowed extensions (e.g., "csv", "xlsx")
     * @return true if extension is allowed
     */
    public static boolean validateFileExtension(String fileName, String... allowedExtensions) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        for (String allowed : allowedExtensions) {
            if (extension.equals(allowed.toLowerCase())) {
                log.info("File extension validated: {} matches {}", fileName, allowed);
                return true;
            }
        }
        
        log.warn("File extension not allowed: {}", fileName);
        return false;
    }
}
