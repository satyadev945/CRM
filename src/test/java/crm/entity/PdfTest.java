package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfTest {

    private Pdf pdf;

    @BeforeEach
    void setUp() {
        pdf = Pdf.builder()
                .id(1L)
                .name("TestPdf")
                .content("Test content for PDF")
                .build();
    }

    @Test
    void builder_shouldCreatePdfWithAllFields() {
        assertNotNull(pdf);
        assertEquals(1L, pdf.getId());
        assertEquals("TestPdf", pdf.getName());
        assertEquals("Test content for PDF", pdf.getContent());
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyPdf() {
        Pdf emptyPdf = new Pdf();
        
        assertNotNull(emptyPdf);
        assertNull(emptyPdf.getId());
        assertNull(emptyPdf.getName());
    }

    @Test
    void allArgsConstructor_shouldCreatePdfWithAllFields() {
        Pdf newPdf = new Pdf(2L, "NewPdf", "New content");

        assertNotNull(newPdf);
        assertEquals(2L, newPdf.getId());
        assertEquals("NewPdf", newPdf.getName());
        assertEquals("New content", newPdf.getContent());
    }

    @Test
    void setAndGetId_shouldWorkCorrectly() {
        pdf.setId(100L);
        assertEquals(100L, pdf.getId());
    }

    @Test
    void setAndGetName_shouldWorkCorrectly() {
        pdf.setName("UpdatedPdf");
        assertEquals("UpdatedPdf", pdf.getName());
    }

    @Test
    void setAndGetContent_shouldWorkCorrectly() {
        pdf.setContent("Updated content");
        assertEquals("Updated content", pdf.getContent());
    }

    @Test
    void pdf_withNullValues_shouldHandleGracefully() {
        Pdf nullPdf = Pdf.builder().build();
        
        assertNull(nullPdf.getId());
        assertNull(nullPdf.getName());
        assertNull(nullPdf.getContent());
    }

    @Test
    void pdf_withEmptyName_shouldHandleGracefully() {
        pdf.setName("");
        assertEquals("", pdf.getName());
    }

    @Test
    void pdf_withLongContent_shouldHandleGracefully() {
        String longContent = "A".repeat(10000);
        pdf.setContent(longContent);
        assertEquals(longContent, pdf.getContent());
    }

    @Test
    void pdf_equalsAndHashCode_shouldWorkCorrectly() {
        Pdf pdf1 = Pdf.builder()
                .id(1L)
                .name("Test")
                .content("Content")
                .build();

        Pdf pdf2 = Pdf.builder()
                .id(1L)
                .name("Test")
                .content("Content")
                .build();

        assertEquals(pdf1, pdf2);
        assertEquals(pdf1.hashCode(), pdf2.hashCode());
    }

    @Test
    void pdf_toString_shouldReturnString() {
        String result = pdf.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("TestPdf"));
    }
}
