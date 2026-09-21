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
        pdf = new Pdf();
        pdf.setId(1L);
        pdf.setName("test-document");
        pdf.setContent("PDF content");
    }

    @Test
    void testConstructor() {
        PdfServiceImpl service = new PdfServiceImpl(pdfRepository);
        assertNotNull(service);
    }

    @Test
    void testFindByName_found() {
        when(pdfRepository.findByName("test-document")).thenReturn(pdf);
        Pdf result = pdfService.findByName("test-document");
        assertNotNull(result);
        assertEquals(pdf, result);
        verify(pdfRepository).findByName("test-document");
    }

    @Test
    void testFindByName_notFound() {
        when(pdfRepository.findByName("nonexistent")).thenReturn(null);
        Pdf result = pdfService.findByName("nonexistent");
        assertNull(result);
        verify(pdfRepository).findByName("nonexistent");
    }

    @Test
    void testFindByName_returnsCorrectPdf() {
        Pdf anotherPdf = new Pdf();
        anotherPdf.setId(2L);
        anotherPdf.setName("another-doc");
        anotherPdf.setContent("Another content");

        when(pdfRepository.findByName("another-doc")).thenReturn(anotherPdf);
        Pdf result = pdfService.findByName("another-doc");
        assertEquals("another-doc", result.getName());
        assertEquals(2L, result.getId());
    }

    @Test
    void testSavePdf() {
        pdfService.savePdf(pdf);
        verify(pdfRepository).save(pdf);
    }

    @Test
    void testSavePdf_newPdf() {
        Pdf newPdf = new Pdf();
        newPdf.setName("new-document");
        newPdf.setContent("New content");
        pdfService.savePdf(newPdf);
        verify(pdfRepository).save(newPdf);
    }

    @Test
    void testSavePdf_callsRepositorySave() {
        Pdf pdfToSave = Pdf.builder()
                .id(3L)
                .name("save-test")
                .content("save content")
                .build();
        pdfService.savePdf(pdfToSave);
        verify(pdfRepository, times(1)).save(pdfToSave);
    }
}
