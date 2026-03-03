package crm.csv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CSVTestTest {

    @Test
    void csvTest_classExists() {
        assertNotNull(CSVTest.class);
    }

    @Test
    void csvTest_hasMainMethod() throws NoSuchMethodException {
        assertNotNull(CSVTest.class.getMethod("main", String[].class));
    }

    @Test
    void csvTest_shouldBeInstantiable() {
        assertDoesNotThrow(() -> new CSVTest());
    }
}
