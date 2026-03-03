package crm.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfViewTest {

    @Test
    void pdfView_shouldBeInstantiable() {
        PdfView pdfView = new PdfView();
        assertNotNull(pdfView);
    }

    @Test
    void pdfView_shouldExtendAbstractPdfView() {
        PdfView pdfView = new PdfView();
        assertTrue(pdfView instanceof AbstractPdfView);
    }

    @Test
    void pdfView_shouldSetContentType() {
        PdfView pdfView = new PdfView();
        assertEquals("application/pdf", pdfView.getContentType());
    }
}
