package crm.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CsvViewTest {

    private CsvView csvView;

    @BeforeEach
    void setUp() {
        csvView = new CsvView();
    }

    @Test
    void testInstantiation() {
        assertNotNull(csvView);
    }

    @Test
    void testContentType() {
        assertEquals("text/csv", csvView.getContentType());
    }

    @Test
    void testGeneratesDownloadContent() {
        assertTrue(csvView.generatesDownloadContent());
    }

    @Test
    void testCsvViewIsInstanceOfAbstractCsvView() {
        assertTrue(csvView instanceof AbstractCsvView);
    }
}
