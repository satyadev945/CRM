package crm.csv;

import lombok.extern.slf4j.Slf4j;

/**
 * @deprecated This class is deprecated and should not be used in production.
 * It uses desktop GUI components and direct file operations which are not cloud-compatible.
 * Use crm.service.CsvService and crm.controller.FileUploadController instead.
 */
@Deprecated
@Slf4j
public class CSVTest {

    /**
     * @deprecated This main method is for testing only and should not be used in production.
     */
    @Deprecated
    public static void main(String[] args) {
        log.error("CSVTest.main called - this class is not cloud-compatible and should not be used in production");
        throw new UnsupportedOperationException(
            "CSVTest is not supported in cloud environments. Use CsvService and web-based file uploads instead."
        );
    }
}
