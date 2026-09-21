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

    private Pdf pdf;

    @BeforeEach
    void setUp() {
        pdf = Pdf.builder()
                .id(1L)
                .name("report.pdf")
                .content("PDF content")
                .build();
    }

    @Test
    void testFindByName_ReturnsExistingPdf() {
        when(pdfRepository.findByName("report.pdf")).thenReturn(pdf);
        Pdf result = pdfService.findByName("report.pdf");
        assertNotNull(result);
        assertEquals(pdf, result);
        verify(pdfRepository).findByName("report.pdf");
    }

    @Test
    void testFindByName_NotFound_ReturnsNull() {
        when(pdfRepository.findByName("nonexistent.pdf")).thenReturn(null);
        Pdf result = pdfService.findByName("nonexistent.pdf");
        assertNull(result);
        verify(pdfRepository).findByName("nonexistent.pdf");
    }

    @Test
    void testSavePdf_SavesSuccessfully() {
        pdfService.savePdf(pdf);
        verify(pdfRepository).save(pdf);
    }

    @Test
    void testSavePdf_VerifyCalledOnce() {
        pdfService.savePdf(pdf);
        verify(pdfRepository, times(1)).save(pdf);
    }

    @Test
    void testSavePdf_WithNewPdf() {
        Pdf newPdf = Pdf.builder().name("invoice.pdf").content("Invoice content").build();
        pdfService.savePdf(newPdf);
        verify(pdfRepository).save(newPdf);
    }

    @Test
    void testConstructor_WithPdfRepository() {
        PdfServiceImpl service = new PdfServiceImpl(pdfRepository);
        assertNotNull(service);
    }

    @Test
    void testFindByName_WithDifferentNames() {
        Pdf pdf2 = Pdf.builder().id(2L).name("invoice.pdf").build();
        when(pdfRepository.findByName("invoice.pdf")).thenReturn(pdf2);
        Pdf result = pdfService.findByName("invoice.pdf");
        assertEquals(pdf2, result);
        assertEquals("invoice.pdf", result.getName());
    }

    @Test
    void testFindByName_EmptyString_ReturnsNull() {
        when(pdfRepository.findByName("")).thenReturn(null);
        Pdf result = pdfService.findByName("");
        assertNull(result);
        verify(pdfRepository).findByName("");
    }
}
