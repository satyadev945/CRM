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
    void csvView_shouldExtendAbstractCsvView() {
        assertTrue(csvView instanceof AbstractCsvView);
    }

    @Test
    void csvView_shouldNotBeNull() {
        assertNotNull(csvView);
    }

    @Test
    void csvView_shouldBeInstantiable() {
        CsvView view = new CsvView();
        assertNotNull(view);
    }
}
