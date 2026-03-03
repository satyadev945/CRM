package crm.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CsvViewTest {

    @Test
    void csvView_shouldBeInstantiable() {
        CsvView csvView = new CsvView();
        assertNotNull(csvView);
    }

    @Test
    void csvView_shouldExtendAbstractCsvView() {
        CsvView csvView = new CsvView();
        assertTrue(csvView instanceof AbstractCsvView);
    }

    @Test
    void csvView_shouldSetContentType() {
        CsvView csvView = new CsvView();
        assertEquals("text/csv", csvView.getContentType());
    }
}
