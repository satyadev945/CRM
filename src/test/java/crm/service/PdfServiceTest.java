package crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for PdfService interface
 */
class PdfServiceTest {

    @Test
    void testPdfServiceInterface() {
        // Verify interface exists
        assertTrue(PdfService.class.isInterface());

        try {
            PdfService.class.getMethod("findByName", String.class);
        } catch (NoSuchMethodException e) {
            fail("PdfService interface missing expected methods");
        }
    }
}
