package crm.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Cloud-ready file reading utility
 * Replaces desktop GUI file chooser with classpath resource loading
 * For cloud deployment, files should be uploaded via REST API or read from classpath
 */
@Slf4j
public class ReadDataUtils {

    /**
     * Read file from classpath resources (cloud-compatible)
     * This method is deprecated and should not be used in cloud environments
     * 
     * @deprecated Use REST API file upload or classpath resources instead
     * @param dialogMessage Not used in cloud environment
     * @param parent Not used in cloud environment
     * @param fileExtensionDescription Not used in cloud environment
     * @param fileExtension File extensions to filter
     * @return null (GUI file chooser not supported in cloud)
     */
    @Deprecated
    public static File ReadFile(String dialogMessage, Object parent, String fileExtensionDescription,
                                String... fileExtension) {
        log.warn("ReadFile method called - Desktop GUI file chooser is not supported in cloud environments");
        log.warn("Please use REST API file upload or classpath resources instead");
        log.warn("Requested file type: {}", fileExtensionDescription);
        
        // Return null as GUI file chooser cannot work in cloud/server environment
        return null;
    }

    /**
     * Read file from classpath resources (cloud-compatible alternative)
     * Use this method to read files bundled with the application
     * 
     * @param resourcePath Path to resource in classpath (e.g., "data/sample.csv")
     * @return InputStream to read the resource
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
     * Check if a classpath resource exists
     * 
     * @param resourcePath Path to resource in classpath
     * @return true if resource exists
     */
    public static boolean resourceExists(String resourcePath) {
        try {
            Resource resource = new ClassPathResource(resourcePath);
            return resource.exists();
        } catch (Exception e) {
            log.error("Error checking resource existence: {}", resourcePath, e);
            return false;
        }
    }
}
