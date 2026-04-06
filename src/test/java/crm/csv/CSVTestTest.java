package crm.csv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CSVTestTest {

    @Test
    void csvTest_shouldHaveMainMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(CSVTest.class.getMethod("main", String[].class));
    }

    @Test
    void csvTest_classExists() {
        // Assert
        assertNotNull(CSVTest.class);
    }

    @Test
    void csvTest_shouldBeInstantiable() {
        // Act
        CSVTest csvTest = new CSVTest();

        // Assert
        assertNotNull(csvTest);
    }
}
