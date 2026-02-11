package crm.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for PdfView class
 */
class PdfViewTest {

    @Test
    void testPdfViewClassExists() {
        assertNotNull(PdfView.class);
    }

    @Test
    void testPdfViewCanBeInstantiated() {
        assertDoesNotThrow(() -> new PdfView());
    }

    @Test
    void testPdfViewExtendsAbstractPdfView() {
        PdfView pdfView = new PdfView();
        assertNotNull(pdfView);
        assertTrue(pdfView instanceof AbstractPdfView);
    }
}
