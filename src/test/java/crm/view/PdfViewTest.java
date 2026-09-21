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
    void testInstantiation() {
        assertNotNull(pdfView);
    }

    @Test
    void testContentType() {
        assertEquals("application/pdf", pdfView.getContentType());
    }

    @Test
    void testGeneratesDownloadContent() {
        assertTrue(pdfView.generatesDownloadContent());
    }

    @Test
    void testPdfViewIsInstanceOfAbstractPdfView() {
        assertTrue(pdfView instanceof AbstractPdfView);
    }
}
