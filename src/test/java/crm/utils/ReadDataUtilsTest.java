package crm.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadDataUtilsTest {

    @Test
    void readDataUtils_shouldBeInstantiable() {
        ReadDataUtils utils = new ReadDataUtils();
        assertNotNull(utils);
    }

    @Test
    void readDataUtils_classExists() {
        assertNotNull(ReadDataUtils.class);
    }

    @Test
    void readFile_methodExists() {
        assertDoesNotThrow(() -> {
            ReadDataUtils.class.getMethod("ReadFile", String.class, javax.swing.JFrame.class, String.class, String[].class);
        });
    }
}
