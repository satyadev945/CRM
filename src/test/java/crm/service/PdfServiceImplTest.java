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
        testPdf = Pdf.builder()
                .id(1L)
                .name("TestPdf")
                .content("Test content")
                .build();
    }

    @Test
    void findByName_shouldReturnPdf() {
        when(pdfRepository.findByName("TestPdf")).thenReturn(testPdf);

        Pdf result = pdfService.findByName("TestPdf");

        assertNotNull(result);
        assertEquals("TestPdf", result.getName());
        verify(pdfRepository).findByName("TestPdf");
    }

    @Test
    void findByName_withNonExistentName_shouldReturnNull() {
        when(pdfRepository.findByName("NonExistent")).thenReturn(null);

        Pdf result = pdfService.findByName("NonExistent");

        assertNull(result);
        verify(pdfRepository).findByName("NonExistent");
    }

    @Test
    void savePdf_shouldSavePdf() {
        when(pdfRepository.save(testPdf)).thenReturn(testPdf);

        pdfService.savePdf(testPdf);

        verify(pdfRepository).save(testPdf);
    }

    @Test
    void savePdf_withNullPdf_shouldHandleGracefully() {
        when(pdfRepository.save(null)).thenReturn(null);

        assertDoesNotThrow(() -> pdfService.savePdf(null));
        verify(pdfRepository).save(null);
    }
}
