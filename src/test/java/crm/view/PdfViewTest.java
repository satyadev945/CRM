package crm.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfViewTest {

    private PdfView pdfView;

    @BeforeEach
    void setUp() {
        pdfView = new PdfView();
    }

    @Test
    void pdfView_shouldExtendAbstractPdfView() {
        // Assert
        assertTrue(pdfView instanceof AbstractPdfView);
    }

    @Test
    void pdfView_shouldBeInstantiable() {
        // Assert
        assertNotNull(pdfView);
    }

    @Test
    void pdfView_shouldHaveBuildPdfDocumentMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(PdfView.class.getDeclaredMethod("buildPdfDocument", 
                java.util.Map.class, 
                com.itextpdf.text.Document.class,
                com.itextpdf.text.pdf.PdfWriter.class,
                javax.servlet.http.HttpServletRequest.class, 
                javax.servlet.http.HttpServletResponse.class));
    }

    @Test
    void pdfView_shouldSetCorrectContentType() {
        // Assert
        assertEquals("application/pdf", pdfView.getContentType());
    }
}
