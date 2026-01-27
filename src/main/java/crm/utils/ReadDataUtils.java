package crm.utils;

import java.io.File;

/**
 * @deprecated This class uses GUI components (Swing) which are not compatible with containerized environments.
 * Use REST API file upload endpoints with MultipartFile instead.
 * This class is kept for backward compatibility but should not be used in production.
 */
@Deprecated
public class ReadDataUtils {

    /**
     * @deprecated This method uses JFileChooser which requires a graphical display and will fail in headless containers.
     * Replace with REST API file upload: @PostMapping("/upload") public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file)
     */
    @Deprecated
    public static File ReadFile(String dialogMEssage, Object parent, String fileExtensionDescription,
                                String... fileExtension) {
        throw new UnsupportedOperationException(
            "GUI-based file selection is not supported in containerized environments. " +
            "Please use REST API file upload endpoints instead."
        );
    }

}
