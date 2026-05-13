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
                .content("Report content")
                .build();
    }

    @Test
    void testConstructor() {
        PdfServiceImpl service = new PdfServiceImpl(pdfRepository);
        assertNotNull(service);
    }

    @Test
    void testFindByName_returnsPdf() {
        when(pdfRepository.findByName("report.pdf")).thenReturn(pdf);
        Pdf result = pdfService.findByName("report.pdf");
        assertNotNull(result);
        assertEquals("report.pdf", result.getName());
        verify(pdfRepository).findByName("report.pdf");
    }

    @Test
    void testFindByName_returnsNull_whenNotFound() {
        when(pdfRepository.findByName("unknown.pdf")).thenReturn(null);
        Pdf result = pdfService.findByName("unknown.pdf");
        assertNull(result);
    }

    @Test
    void testSavePdf() {
        pdfService.savePdf(pdf);
        verify(pdfRepository).save(pdf);
    }

    @Test
    void testSavePdf_withNewPdf() {
        Pdf newPdf = new Pdf();
        newPdf.setName("new.pdf");
        newPdf.setContent("New content");
        pdfService.savePdf(newPdf);
        verify(pdfRepository).save(newPdf);
    }

    @Test
    void testFindByName_withEmptyString() {
        when(pdfRepository.findByName("")).thenReturn(null);
        Pdf result = pdfService.findByName("");
        assertNull(result);
    }

    @Test
    void testFindByName_withDifferentExtension() {
        Pdf txtPdf = Pdf.builder().id(2L).name("document").build();
        when(pdfRepository.findByName("document")).thenReturn(txtPdf);
        Pdf result = pdfService.findByName("document");
        assertNotNull(result);
        assertEquals("document", result.getName());
    }
}
