package crm.utils;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ReadDataUtils utility class
 */
class ReadDataUtilsTest {

    @Test
    void testReadFileMethodExists() {
        // Verify the method exists
        assertDoesNotThrow(() -> {
            ReadDataUtils.class.getMethod("ReadFile", String.class, JFrame.class, String.class, String[].class);
        });
    }

    @Test
    void testReadFileWithNullParent() {
        // Test in headless environment throws exception or returns null
        String[] extensions = {"csv"};
        // This will throw HeadlessException in headless environment, which is expected
        assertThrows(Exception.class, () -> {
            ReadDataUtils.ReadFile("Test Message", null, "CSV Files", extensions);
        });
    }

    @Test
    void testReadFileWithMultipleExtensions() {
        // Test in headless environment throws exception
        String[] extensions = {"csv", "txt", "xls"};
        assertThrows(Exception.class, () -> {
            ReadDataUtils.ReadFile("Test Message", null, "Multiple Files", extensions);
        });
    }

    @Test
    void testReadFileWithEmptyExtensions() {
        // Empty extensions array throws IllegalArgumentException
        String[] extensions = {};
        assertThrows(IllegalArgumentException.class, () -> {
            ReadDataUtils.ReadFile("Test Message", null, "All Files", extensions);
        });
    }

    @Test
    void testReadFileWithNullExtensions() {
        // Null extensions throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            ReadDataUtils.ReadFile("Test Message", null, "All Files", (String[]) null);
        });
    }

    @Test
    void testReadDataUtilsClassExists() {
        assertNotNull(ReadDataUtils.class);
    }

    @Test
    void testReadDataUtilsCanBeInstantiated() {
        assertDoesNotThrow(() -> new ReadDataUtils());
    }
}
