package crm.utils;

import java.io.File;

public class ReadDataUtils {

    /**
     * This method is updated to be cloud-ready. 
     * In a cloud environment, JFileChooser (Swing) is not applicable.
     * File operations should be handled via Azure Blob Storage or multipart file uploads.
     */
    public static File ReadFile(String dialogMEssage, Object parent, String fileExtensionDescription,
                                String... fileExtension) {
        // In a real cloud migration, this would be replaced by a service that 
        // interacts with Azure Blob Storage or handles an uploaded InputStream.
        throw new UnsupportedOperationException("Local file selection via JFileChooser is not supported in cloud environments. Please use Azure Blob Storage.");
    }

}
