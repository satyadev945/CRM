package crm.controller;

import crm.entity.Pdf;
import crm.service.PdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfControllerTest {

    @Mock
    private PdfService pdfService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private PdfController pdfController;

    private Pdf testPdf;

    @BeforeEach
    void setUp() {
        testPdf = new Pdf();
        testPdf.setId(1L);
        testPdf.setName("test-document");
        testPdf.setContent("This is test content for the PDF document.");
    }

    @Test
    void constructor_shouldInitializePdfService() {
        // Assert
        assertNotNull(pdfController);
    }

    @Test
    void pdfGenerator_shouldReturnGeneratorView() {
        // Act
        String viewName = pdfController.pdfGenerator(model);

        // Assert
        assertEquals("pdf/generator", viewName);
        verify(model).addAttribute(eq("pdf"), any(Pdf.class));
    }

    @Test
    void generatePdf_withValidPdf_shouldReturnSuccessView() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        // Assert
        assertEquals("pdf/success", viewName);
        verify(pdfService).savePdf(testPdf);
    }

    @Test
    void generatePdf_withInvalidPdf_shouldRedirectToGenerator() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        // Assert
        assertEquals("redirect:/pdf-generator", viewName);
        verify(pdfService, never()).savePdf(any());
    }

    @Test
    void generatePdf_withPdfNameWithoutExtension_shouldAddExtension() {
        // Arrange
        testPdf.setName("document-without-extension");
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        // Assert
        assertEquals("pdf/success", viewName);
        verify(pdfService).savePdf(testPdf);
    }

    @Test
    void generatePdf_withPdfNameWithExtension_shouldNotDuplicateExtension() {
        // Arrange
        testPdf.setName("document.pdf");
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        // Assert
        assertEquals("pdf/success", viewName);
        verify(pdfService).savePdf(testPdf);
    }

    @Test
    void pdfController_shouldHaveControllerAnnotation() {
        // Assert
        assertTrue(PdfController.class.isAnnotationPresent(org.springframework.stereotype.Controller.class));
    }
}
