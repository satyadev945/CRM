package crm.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @deprecated This class uses Swing GUI components which are incompatible with cloud environments.
 * Use {@link FileUtils} instead for cloud-compatible file operations.
 */
@Deprecated
@Slf4j
public class ReadDataUtils {

    /**
     * @deprecated This method uses Swing JFileChooser which is incompatible with web applications.
     * Use web-based file upload mechanisms instead.
     */
    @Deprecated
    public static java.io.File ReadFile(String dialogMEssage, Object parent, String fileExtensionDescription,
                                String... fileExtension) {
        log.error("ReadFile called in cloud environment - this method uses Swing GUI components which are not supported");
        throw new UnsupportedOperationException(
            "ReadDataUtils.ReadFile is not supported in cloud environments. Use web-based file uploads instead."
        );
    }
}
