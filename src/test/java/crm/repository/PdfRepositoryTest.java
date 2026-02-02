package crm.repository;

import crm.entity.Pdf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PdfRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PdfRepository pdfRepository;

    @Test
    void findByNameShouldReturnPdf() {
        // Arrange
        Pdf pdf = new Pdf();
        pdf.setName("test.pdf");
        pdf.setContent("Test content");
        entityManager.persistAndFlush(pdf);

        // Act
        Pdf found = pdfRepository.findByName("test.pdf");

        // Assert
        assertNotNull(found);
        assertEquals("test.pdf", found.getName());
    }

    @Test
    void findByNameShouldReturnNullWhenPdfNotFound() {
        // Act
        Pdf found = pdfRepository.findByName("nonexistent.pdf");

        // Assert
        assertNull(found);
    }

    @Test
    void saveShouldPersistPdf() {
        // Arrange
        Pdf pdf = new Pdf();
        pdf.setName("new.pdf");

        // Act
        Pdf saved = pdfRepository.save(pdf);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("new.pdf", saved.getName());

        // Verify it's in the database
        Pdf found = entityManager.find(Pdf.class, saved.getId());
        assertNotNull(found);
        assertEquals("new.pdf", found.getName());
    }

    @Test
    void deleteShouldRemovePdf() {
        // Arrange
        Pdf pdf = new Pdf();
        pdf.setName("to-delete.pdf");
        pdf = entityManager.persistAndFlush(pdf);
        Long id = pdf.getId();

        // Act
        pdfRepository.deleteById(id);

        // Assert
        Pdf found = entityManager.find(Pdf.class, id);
        assertNull(found);
    }
}