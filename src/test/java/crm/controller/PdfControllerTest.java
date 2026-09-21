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
import static org.mockito.ArgumentMatchers.*;
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

    private Pdf pdf;

    @BeforeEach
    void setUp() {
        pdf = Pdf.builder()
                .id(1L)
                .name("test-report")
                .content("This is the PDF content for testing.")
                .build();
    }

    @Test
    void testPdfGenerator_ReturnsGeneratorView() {
        String view = pdfController.pdfGenerator(model);
        assertEquals("pdf/generator", view);
    }

    @Test
    void testPdfGenerator_AddsPdfToModel() {
        pdfController.pdfGenerator(model);
        verify(model).addAttribute(eq("pdf"), any(Pdf.class));
    }

    @Test
    void testGeneratePdf_WithErrors_RedirectsToPdfGenerator() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = pdfController.generatePdf(pdf, bindingResult);
        assertEquals("redirect:/pdf-generator", view);
        verify(pdfService, never()).savePdf(any());
    }

    @Test
    void testGeneratePdf_NoErrors_WithPdfExtension_ReturnsSuccessView() {
        pdf.setName("test-report.pdf");
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = pdfController.generatePdf(pdf, bindingResult);

        assertEquals("pdf/success", view);
    }

    @Test
    void testGeneratePdf_NoErrors_WithoutPdfExtension_ReturnsSuccessView() {
        pdf.setName("test-report");
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = pdfController.generatePdf(pdf, bindingResult);

        assertEquals("pdf/success", view);
    }

    @Test
    void testGeneratePdf_NoErrors_SavesPdf() {
        pdf.setName("test-report.pdf");
        when(bindingResult.hasErrors()).thenReturn(false);

        pdfController.generatePdf(pdf, bindingResult);

        verify(pdfService).savePdf(pdf);
    }

    @Test
    void testConstructor_WithPdfService() {
        PdfController controller = new PdfController(pdfService);
        assertNotNull(controller);
    }

    @Test
    void testPdfGenerator_ViewNotNull() {
        String view = pdfController.pdfGenerator(model);
        assertNotNull(view);
    }

    @Test
    void testGeneratePdf_WithErrors_DoesNotSavePdf() {
        when(bindingResult.hasErrors()).thenReturn(true);
        pdfController.generatePdf(pdf, bindingResult);
        verify(pdfService, never()).savePdf(any(Pdf.class));
    }
}
