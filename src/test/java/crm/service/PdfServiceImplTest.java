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
                .name("test-document")
                .content("Test content")
                .build();
    }

    @Test
    void constructor_shouldInitializePdfRepository() {
        // Assert
        assertNotNull(pdfService);
    }

    @Test
    void findByName_shouldReturnPdf() {
        // Arrange
        when(pdfRepository.findByName("test-document")).thenReturn(testPdf);

        // Act
        Pdf result = pdfService.findByName("test-document");

        // Assert
        assertNotNull(result);
        assertEquals("test-document", result.getName());
        verify(pdfRepository).findByName("test-document");
    }

    @Test
    void findByName_shouldReturnNull_whenNotFound() {
        // Arrange
        when(pdfRepository.findByName("nonexistent")).thenReturn(null);

        // Act
        Pdf result = pdfService.findByName("nonexistent");

        // Assert
        assertNull(result);
        verify(pdfRepository).findByName("nonexistent");
    }

    @Test
    void savePdf_shouldSavePdf() {
        // Arrange
        when(pdfRepository.save(testPdf)).thenReturn(testPdf);

        // Act
        pdfService.savePdf(testPdf);

        // Assert
        verify(pdfRepository).save(testPdf);
    }

    @Test
    void savePdf_shouldHandleNewPdf() {
        // Arrange
        Pdf newPdf = Pdf.builder()
                .name("new-document")
                .content("New content")
                .build();
        when(pdfRepository.save(newPdf)).thenReturn(newPdf);

        // Act
        pdfService.savePdf(newPdf);

        // Assert
        verify(pdfRepository).save(newPdf);
    }
}
