package crm.csv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CSVTestTest {

    @Test
    void csvTest_shouldBeInstantiable() {
        CSVTest csvTest = new CSVTest();
        assertNotNull(csvTest);
    }

    @Test
    void csvTest_classExists() {
        assertNotNull(CSVTest.class);
    }
}
