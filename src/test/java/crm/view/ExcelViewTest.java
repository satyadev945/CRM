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
        // Assert
        assertTrue(excelView instanceof AbstractView);
    }

    @Test
    void excelView_shouldBeInstantiable() {
        // Assert
        assertNotNull(excelView);
    }

    @Test
    void excelView_shouldHaveBuildExcelDocumentMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(ExcelView.class.getDeclaredMethod("buildExcelDocument", 
                java.util.Map.class, 
                org.apache.poi.ss.usermodel.Workbook.class,
                javax.servlet.http.HttpServletRequest.class, 
                javax.servlet.http.HttpServletResponse.class));
    }

    @Test
    void excelView_shouldSetCorrectContentType() {
        // Assert
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelView.getContentType());
    }

    @Test
    void excelView_shouldGenerateDownloadContent() throws NoSuchMethodException {
        // Assert
        assertNotNull(ExcelView.class.getDeclaredMethod("generatesDownloadContent"));
    }
}
