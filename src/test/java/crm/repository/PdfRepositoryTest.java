package crm.repository;

import crm.entity.Pdf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PdfRepositoryTest {

    @Autowired
    private PdfRepository pdfRepository;

    @Test
    void pdfRepository_shouldNotBeNull() {
        assertNotNull(pdfRepository);
    }

    @Test
    void save_shouldPersistPdf() {
        Pdf pdf = new Pdf();
        pdf.setName("document.pdf");

        Pdf saved = pdfRepository.save(pdf);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("document.pdf", saved.getName());
    }

    @Test
    void findByName_withExistingName_shouldReturnPdf() {
        Pdf pdf = new Pdf();
        pdf.setName("report.pdf");
        pdfRepository.save(pdf);

        Pdf found = pdfRepository.findByName("report.pdf");

        assertNotNull(found);
        assertEquals("report.pdf", found.getName());
    }

    @Test
    void findByName_withNonExistingName_shouldReturnNull() {
        Pdf found = pdfRepository.findByName("nonexistent.pdf");
        assertNull(found);
    }

    @Test
    void findById_withExistingId_shouldReturnPdf() {
        Pdf pdf = new Pdf();
        pdf.setName("invoice.pdf");
        Pdf saved = pdfRepository.save(pdf);

        Pdf found = pdfRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("invoice.pdf", found.getName());
    }

    @Test
    void delete_shouldRemovePdf() {
        Pdf pdf = new Pdf();
        pdf.setName("todelete.pdf");
        Pdf saved = pdfRepository.save(pdf);

        pdfRepository.delete(saved);

        assertFalse(pdfRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void findAll_shouldReturnAllPdfs() {
        Pdf pdf1 = new Pdf();
        pdf1.setName("pdf1.pdf");
        pdfRepository.save(pdf1);

        Pdf pdf2 = new Pdf();
        pdf2.setName("pdf2.pdf");
        pdfRepository.save(pdf2);

        assertTrue(pdfRepository.findAll().size() >= 2);
    }

    @Test
    void count_shouldReturnNumberOfPdfs() {
        long initialCount = pdfRepository.count();

        Pdf pdf = new Pdf();
        pdf.setName("newpdf.pdf");
        pdfRepository.save(pdf);

        assertEquals(initialCount + 1, pdfRepository.count());
    }
}
