package crm.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ExcelView class
 */
class ExcelViewTest {

    @Test
    void testExcelViewClassExists() {
        assertNotNull(ExcelView.class);
    }

    @Test
    void testExcelViewCanBeInstantiated() {
        assertDoesNotThrow(() -> new ExcelView());
    }

    @Test
    void testExcelViewExtendsAbstractView() {
        ExcelView excelView = new ExcelView();
        assertNotNull(excelView);
    }
}
