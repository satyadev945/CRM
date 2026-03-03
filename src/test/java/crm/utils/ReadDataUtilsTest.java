package crm.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadDataUtilsTest {

    @Test
    void readDataUtils_classExists() {
        assertNotNull(ReadDataUtils.class);
    }

    @Test
    void readDataUtils_hasReadFileMethod() throws NoSuchMethodException {
        assertNotNull(ReadDataUtils.class.getMethod("ReadFile", String.class, javax.swing.JFrame.class, String.class, String[].class));
    }

    @Test
    void readDataUtils_shouldBeInstantiable() {
        assertDoesNotThrow(() -> new ReadDataUtils());
    }
}
