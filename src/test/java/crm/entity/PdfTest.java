package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfTest {

    private Pdf pdf;

    @BeforeEach
    void setUp() {
        pdf = new Pdf();
        pdf.setId(1L);
        pdf.setName("test-document");
        pdf.setContent("This is the PDF content");
    }

    @Test
    void testPdfDefaultConstructor() {
        Pdf newPdf = new Pdf();
        assertNotNull(newPdf);
    }

    @Test
    void testPdfAllArgsConstructor() {
        Pdf newPdf = new Pdf(1L, "document", "content");
        assertNotNull(newPdf);
        assertEquals(1L, newPdf.getId());
        assertEquals("document", newPdf.getName());
        assertEquals("content", newPdf.getContent());
    }

    @Test
    void testPdfBuilder() {
        Pdf builtPdf = Pdf.builder()
                .id(2L)
                .name("builder-doc")
                .content("builder content")
                .build();
        assertNotNull(builtPdf);
        assertEquals(2L, builtPdf.getId());
        assertEquals("builder-doc", builtPdf.getName());
        assertEquals("builder content", builtPdf.getContent());
    }

    @Test
    void testGetId() {
        assertEquals(1L, pdf.getId());
    }

    @Test
    void testSetId() {
        pdf.setId(99L);
        assertEquals(99L, pdf.getId());
    }

    @Test
    void testGetName() {
        assertEquals("test-document", pdf.getName());
    }

    @Test
    void testSetName() {
        pdf.setName("new-document");
        assertEquals("new-document", pdf.getName());
    }

    @Test
    void testGetContent() {
        assertEquals("This is the PDF content", pdf.getContent());
    }

    @Test
    void testSetContent() {
        pdf.setContent("New content");
        assertEquals("New content", pdf.getContent());
    }

    @Test
    void testEqualsAndHashCode() {
        Pdf pdf1 = Pdf.builder()
                .id(1L)
                .name("test-document")
                .content("This is the PDF content")
                .build();

        Pdf pdf2 = Pdf.builder()
                .id(1L)
                .name("test-document")
                .content("This is the PDF content")
                .build();

        assertEquals(pdf1, pdf2);
        assertEquals(pdf1.hashCode(), pdf2.hashCode());
    }

    @Test
    void testToString() {
        String toString = pdf.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("test-document"));
    }

    @Test
    void testPdfWithNullContent() {
        pdf.setContent(null);
        assertNull(pdf.getContent());
    }

    @Test
    void testPdfWithNullName() {
        pdf.setName(null);
        assertNull(pdf.getName());
    }

    @Test
    void testPdfWithEmptyName() {
        pdf.setName("");
        assertEquals("", pdf.getName());
    }

    @Test
    void testPdfWithLongContent() {
        String longContent = "A".repeat(10000);
        pdf.setContent(longContent);
        assertEquals(longContent, pdf.getContent());
    }
}
