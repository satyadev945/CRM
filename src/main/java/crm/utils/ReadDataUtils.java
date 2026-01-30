package crm.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * Cloud-ready data utilities.
 * Removed Swing/AWT dependencies which are incompatible with headless cloud environments.
 * File uploads should be handled via REST API endpoints with multipart/form-data.
 */
@Slf4j
public class ReadDataUtils {

    /**
     * DEPRECATED: Swing-based file chooser not compatible with cloud environments.
     * Use REST API file upload endpoints instead.
     *
     * In cloud environments, implement file upload as:
     * @PostMapping("/api/upload")
     * public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
     *     InputStream inputStream = file.getInputStream();
     *     // Process file or upload to S3
     * }
     *
     * @deprecated Use REST API endpoints with MultipartFile for cloud deployment
     */
    @Deprecated
    public static void ReadFile(String dialogMessage, Object parent, String fileExtensionDescription,
                                String... fileExtension) {
        log.error("Swing file chooser is not supported in headless cloud environments. " +
                "Please use REST API endpoints with MultipartFile uploads instead.");
        throw new UnsupportedOperationException(
                "GUI file chooser not available in cloud environment. Use REST API file upload endpoint."
        );
    }

    /**
     * Cloud-compatible method to read file from classpath resources.
     * Use this for reading configuration files or static resources.
     *
     * @param resourcePath Path to resource in classpath (e.g., "config/data.csv")
     * @return InputStream for the resource, or null if not found
     */
    public static InputStream readClasspathResource(String resourcePath) {
        log.info("Reading classpath resource: {}", resourcePath);
        InputStream inputStream = ReadDataUtils.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            log.warn("Classpath resource not found: {}", resourcePath);
        } else {
            log.info("Successfully loaded classpath resource: {}", resourcePath);
        }

        return inputStream;
    }

    /**
     * For cloud deployment, files should be uploaded via REST endpoints.
     * Example controller method:
     *
     * @RestController
     * @RequestMapping("/api/files")
     * public class FileUploadController {
     *
     *     @PostMapping("/upload")
     *     public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
     *         try {
     *             String fileName = file.getOriginalFilename();
     *             InputStream inputStream = file.getInputStream();
     *
     *             // Option 1: Process immediately
     *             processFile(inputStream);
     *
     *             // Option 2: Upload to S3 for persistent storage
     *             // s3Client.putObject(bucketName, fileName, inputStream, metadata);
     *
     *             return ResponseEntity.ok("File uploaded successfully: " + fileName);
     *         } catch (IOException e) {
     *             return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
     *         }
     *     }
     * }
     */
}
