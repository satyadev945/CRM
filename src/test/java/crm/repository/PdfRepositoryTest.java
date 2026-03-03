package crm.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfRepositoryTest {

    @Test
    void pdfRepository_interfaceExists() {
        assertNotNull(PdfRepository.class);
    }

    @Test
    void pdfRepository_hasFindByNameMethod() throws NoSuchMethodException {
        assertNotNull(PdfRepository.class.getMethod("findByName", String.class));
    }
}
