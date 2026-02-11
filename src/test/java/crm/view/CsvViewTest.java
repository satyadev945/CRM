package crm.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for CsvView class
 */
class CsvViewTest {

    @Test
    void testCsvViewClassExists() {
        assertNotNull(CsvView.class);
    }

    @Test
    void testCsvViewCanBeInstantiated() {
        assertDoesNotThrow(() -> new CsvView());
    }

    @Test
    void testCsvViewExtendsAbstractCsvView() {
        CsvView csvView = new CsvView();
        assertNotNull(csvView);
        assertTrue(csvView instanceof AbstractCsvView);
    }
}
