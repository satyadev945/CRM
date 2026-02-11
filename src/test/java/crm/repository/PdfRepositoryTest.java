package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for PdfRepository interface
 */
class PdfRepositoryTest {

    @Test
    void testPdfRepositoryInterface() {
        // Verify interface exists and extends JpaRepository
        assertTrue(PdfRepository.class.isInterface());
        assertTrue(JpaRepository.class.isAssignableFrom(PdfRepository.class));
    }

    @Test
    void testPdfRepositoryMethods() {
        try {
            PdfRepository.class.getMethod("findByName", String.class);
        } catch (NoSuchMethodException e) {
            fail("PdfRepository interface missing expected methods");
        }
    }

    @Test
    void testRepositoryAnnotation() {
        assertTrue(PdfRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }
}
