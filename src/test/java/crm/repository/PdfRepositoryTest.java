package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

class PdfRepositoryTest {

    @Test
    void pdfRepository_shouldExtendJpaRepository() {
        // Assert
        assertTrue(JpaRepository.class.isAssignableFrom(PdfRepository.class));
    }

    @Test
    void pdfRepository_shouldHaveRepositoryAnnotation() {
        // Assert
        assertTrue(PdfRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }

    @Test
    void pdfRepository_shouldHaveFindByNameMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(PdfRepository.class.getMethod("findByName", String.class));
    }
}
