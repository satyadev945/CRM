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
        assertTrue(pdfView instanceof AbstractPdfView);
    }

    @Test
    void pdfView_shouldNotBeNull() {
        assertNotNull(pdfView);
    }

    @Test
    void pdfView_shouldBeInstantiable() {
        PdfView view = new PdfView();
        assertNotNull(view);
    }
}
