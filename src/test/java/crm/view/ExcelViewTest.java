package crm.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcelViewTest {

    private ExcelView excelView;

    @BeforeEach
    void setUp() {
        excelView = new ExcelView();
    }

    @Test
    void testInstantiation() {
        assertNotNull(excelView);
    }

    @Test
    void testExcelViewIsNotNull() {
        ExcelView view = new ExcelView();
        assertNotNull(view);
    }
}
