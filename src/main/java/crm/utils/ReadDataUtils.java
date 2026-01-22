package crm.utils;

import java.io.File;

public class ReadDataUtils {

    public static File ReadFile(String dialogMessage, String filePath, String fileExtensionDescription,
                                String... fileExtension) {
        // GUI-based file chooser removed for container compatibility
        // File path should be provided via API parameter or environment variable
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path must be provided. GUI file chooser not available in containerized environment.");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }
        // Validate file extension if needed
        if (fileExtension != null && fileExtension.length > 0) {
            boolean validExtension = false;
            for (String ext : fileExtension) {
                if (file.getName().endsWith("." + ext)) {
                    validExtension = true;
                    break;
                }
            }
            if (!validExtension) {
                throw new IllegalArgumentException("Invalid file extension. Expected: " + String.join(", ", fileExtension));
            }
        }
        return file;
    }

}
