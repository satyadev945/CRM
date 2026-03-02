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
        testPdf.setContent("Test PDF content");
    }

    @Test
    void constructor_withPdfService_shouldCreateInstance() {
        PdfController controller = new PdfController(pdfService);
        assertNotNull(controller);
    }

    @Test
    void pdfGenerator_shouldReturnGeneratorView() {
        String viewName = pdfController.pdfGenerator(model);

        assertEquals("pdf/generator", viewName);
        verify(model).addAttribute(eq("pdf"), any(Pdf.class));
    }

    @Test
    void generatePdf_withValidPdf_shouldReturnSuccessView() {
        when(bindingResult.hasErrors()).thenReturn(false);
        testPdf.setName("test.pdf");

        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        assertEquals("pdf/success", viewName);
    }

    @Test
    void generatePdf_withErrors_shouldRedirectToGenerator() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        assertEquals("redirect:/pdf-generator", viewName);
        verify(pdfService, never()).savePdf(any());
    }

    @Test
    void generatePdf_withValidPdf_shouldSavePdf() {
        when(bindingResult.hasErrors()).thenReturn(false);
        testPdf.setName("test.pdf");

        pdfController.generatePdf(testPdf, bindingResult);

        verify(pdfService).savePdf(testPdf);
    }

    @Test
    void generatePdf_withPdfNameWithoutExtension_shouldHandleGracefully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        testPdf.setName("test-without-extension");

        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        assertEquals("pdf/success", viewName);
    }

    @Test
    void pdfController_shouldNotBeNull() {
        assertNotNull(pdfController);
    }

    @Test
    void pdfGenerator_multipleInvocations_shouldAddAttributeEachTime() {
        pdfController.pdfGenerator(model);
        pdfController.pdfGenerator(model);

        verify(model, times(2)).addAttribute(eq("pdf"), any(Pdf.class));
    }

    @Test
    void generatePdf_withNullContent_shouldHandleGracefully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        testPdf.setContent(null);
        testPdf.setName("test.pdf");

        assertDoesNotThrow(() -> pdfController.generatePdf(testPdf, bindingResult));
    }

    @Test
    void generatePdf_withEmptyName_shouldHandleGracefully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        testPdf.setName("");

        assertDoesNotThrow(() -> pdfController.generatePdf(testPdf, bindingResult));
    }
}
