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
import static org.mockito.ArgumentMatchers.any;
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
        testPdf = Pdf.builder()
                .id(1L)
                .name("TestDocument")
                .content("PDF Content")
                .build();
    }

    @Test
    void testFindByName() {
        when(pdfRepository.findByName("TestDocument")).thenReturn(testPdf);

        Pdf result = pdfService.findByName("TestDocument");

        assertNotNull(result);
        assertEquals("TestDocument", result.getName());
        verify(pdfRepository).findByName("TestDocument");
    }

    @Test
    void testFindByNameNotFound() {
        when(pdfRepository.findByName("NonExistent")).thenReturn(null);

        Pdf result = pdfService.findByName("NonExistent");

        assertNull(result);
        verify(pdfRepository).findByName("NonExistent");
    }

    @Test
    void testSavePdf() {
        when(pdfRepository.save(any(Pdf.class))).thenReturn(testPdf);

        pdfService.savePdf(testPdf);

        verify(pdfRepository).save(testPdf);
    }

    @Test
    void testConstructor() {
        PdfRepository mockRepo = mock(PdfRepository.class);
        PdfServiceImpl service = new PdfServiceImpl(mockRepo);

        assertNotNull(service);
    }

    @Test
    void testSavePdfWithNullName() {
        testPdf.setName(null);
        when(pdfRepository.save(any(Pdf.class))).thenReturn(testPdf);

        pdfService.savePdf(testPdf);

        verify(pdfRepository).save(testPdf);
    }

    @Test
    void testSavePdfWithNullContent() {
        testPdf.setContent(null);
        when(pdfRepository.save(any(Pdf.class))).thenReturn(testPdf);

        pdfService.savePdf(testPdf);

        verify(pdfRepository).save(testPdf);
    }
}
