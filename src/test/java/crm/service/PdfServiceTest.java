package crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfServiceTest {

    @Test
    void pdfService_interfaceExists() {
        assertNotNull(PdfService.class);
    }

    @Test
    void pdfService_isInterface() {
        assertTrue(PdfService.class.isInterface());
    }

    @Test
    void pdfService_hasFindByNameMethod() throws NoSuchMethodException {
        assertNotNull(PdfService.class.getMethod("findByName", String.class));
    }

    @Test
    void pdfService_hasSavePdfMethod() throws NoSuchMethodException {
        assertNotNull(PdfService.class.getMethod("savePdf", crm.entity.Pdf.class));
    }
}
