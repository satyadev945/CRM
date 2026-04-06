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
        // Assert
        assertTrue(csvView instanceof AbstractCsvView);
    }

    @Test
    void csvView_shouldBeInstantiable() {
        // Assert
        assertNotNull(csvView);
    }

    @Test
    void csvView_shouldHaveBuildCsvDocumentMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(CsvView.class.getDeclaredMethod("buildCsvDocument", 
                java.util.Map.class, 
                javax.servlet.http.HttpServletRequest.class, 
                javax.servlet.http.HttpServletResponse.class));
    }

    @Test
    void csvView_shouldSetCorrectContentType() {
        // Assert
        assertEquals("text/csv", csvView.getContentType());
    }
}
