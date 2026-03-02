package crm.service;

import crm.entity.Pdf;
import crm.repository.PdfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {

    @Mock
    private PdfRepository pdfRepository;

    @InjectMocks
    private PdfServiceImpl pdfService;

    private Pdf testPdf;

    @BeforeEach
    void setUp() {
        testPdf = new Pdf(1L, "test.pdf", "Test content");
    }

    @Test
    void constructor_withPdfRepository_shouldCreateInstance() {
        PdfServiceImpl service = new PdfServiceImpl(pdfRepository);
        assertNotNull(service);
    }

    @Test
    void findByName_withExistingName_shouldReturnPdf() {
        when(pdfRepository.findByName("test.pdf")).thenReturn(testPdf);

        Pdf found = pdfService.findByName("test.pdf");

        assertNotNull(found);
        assertEquals("test.pdf", found.getName());
        verify(pdfRepository, times(1)).findByName("test.pdf");
    }

    @Test
    void findByName_withNonExistingName_shouldReturnNull() {
        when(pdfRepository.findByName("nonexistent.pdf")).thenReturn(null);

        Pdf found = pdfService.findByName("nonexistent.pdf");

        assertNull(found);
        verify(pdfRepository, times(1)).findByName("nonexistent.pdf");
    }

    @Test
    void savePdf_shouldCallRepository() {
        pdfService.savePdf(testPdf);

        verify(pdfRepository, times(1)).save(testPdf);
    }

    @Test
    void pdfService_shouldImplementPdfService() {
        assertTrue(pdfService instanceof PdfService);
    }
}
