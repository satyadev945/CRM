package crm.utils;

import java.io.File;

/**
 * Utility class for file operations.
 * 
 * NOTE: The GUI-based file chooser functionality has been removed for containerization compatibility.
 * In containerized environments, file uploads should be handled via HTTP multipart/form-data endpoints.
 * 
 * Example controller implementation:
 * 
 * @PostMapping("/upload")
 * public String handleFileUpload(@RequestParam("file") MultipartFile file) {
 *     if (file.isEmpty()) {
 *         return "redirect:/upload?error";
 *     }
 *     try {
 *         byte[] bytes = file.getBytes();
 *         // Process the file bytes
 *         return "redirect:/upload?success";
 *     } catch (IOException e) {
 *         return "redirect:/upload?error";
 *     }
 * }
 * 
 * @deprecated This class used JFileChooser which is not compatible with headless container environments.
 * Use MultipartFile-based file upload endpoints instead.
 */
@Deprecated
public class ReadDataUtils {

    /**
     * @deprecated This method uses JFileChooser (Swing GUI) which does not work in containerized environments.
     * Replace with API-based file upload using MultipartFile in Spring controllers.
     * 
     * For CSV file uploads, create an endpoint like:
     * @PostMapping("/import-csv")
     * public String handleCsvUpload(@RequestParam("file") MultipartFile file) { ... }
     */
    @Deprecated
    public static File ReadFile(String dialogMessage, Object parent, String fileExtensionDescription,
                                String... fileExtension) {
        throw new UnsupportedOperationException(
            "GUI-based file selection is not supported in containerized environments. " +
            "Please use HTTP multipart/form-data file upload endpoints instead. " +
            "Example: @PostMapping(\"/upload\") with @RequestParam(\"file\") MultipartFile parameter."
        );
    }

}
