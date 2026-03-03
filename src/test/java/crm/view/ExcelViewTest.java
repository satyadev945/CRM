package crm.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcelViewTest {

    @Test
    void excelView_shouldBeInstantiable() {
        ExcelView excelView = new ExcelView();
        assertNotNull(excelView);
    }

    @Test
    void excelView_shouldExtendAbstractXlsView() {
        ExcelView excelView = new ExcelView();
        assertTrue(excelView instanceof org.springframework.web.servlet.view.document.AbstractXlsView);
    }
}
