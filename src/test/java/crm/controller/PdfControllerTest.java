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
import static org.mockito.ArgumentMatchers.any;
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
        testPdf = Pdf.builder()
                .name("TestDocument")
                .content("Test PDF Content")
                .build();
    }

    @Test
    void testConstructor() {
        PdfService mockService = mock(PdfService.class);
        PdfController controller = new PdfController(mockService);
        assertNotNull(controller);
    }

    @Test
    void testPdfGenerator() {
        String viewName = pdfController.pdfGenerator(model);

        assertEquals("pdf/generator", viewName);
        verify(model).addAttribute(eq("pdf"), any(Pdf.class));
    }

    @Test
    void testGeneratePdfSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        assertEquals("pdf/success", viewName);
        verify(pdfService).savePdf(testPdf);
    }

    @Test
    void testGeneratePdfWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        assertEquals("redirect:/pdf-generator", viewName);
        verify(pdfService, never()).savePdf(any());
    }

    @Test
    void testGeneratePdfWithInvalidName() {
        testPdf.setName("");
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = pdfController.generatePdf(testPdf, bindingResult);

        assertEquals("pdf/success", viewName);
    }
}
