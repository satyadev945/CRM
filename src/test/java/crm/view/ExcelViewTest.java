package crm.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.AbstractView;

import static org.junit.jupiter.api.Assertions.*;

class ExcelViewTest {

    private ExcelView excelView;

    @BeforeEach
    void setUp() {
        excelView = new ExcelView();
    }

    @Test
    void excelView_shouldExtendAbstractView() {
        assertTrue(excelView instanceof AbstractView);
    }

    @Test
    void excelView_shouldNotBeNull() {
        assertNotNull(excelView);
    }

    @Test
    void excelView_shouldBeInstantiable() {
        ExcelView view = new ExcelView();
        assertNotNull(view);
    }
}
