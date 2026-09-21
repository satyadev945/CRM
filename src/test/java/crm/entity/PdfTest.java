package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfTest {

    private Pdf pdf;

    @BeforeEach
    void setUp() {
        pdf = new Pdf();
    }

    @Test
    void testDefaultConstructor() {
        Pdf p = new Pdf();
        assertNotNull(p);
        assertNull(p.getId());
        assertNull(p.getName());
        assertNull(p.getContent());
    }

    @Test
    void testAllArgsConstructor() {
        Pdf p = new Pdf(1L, "report.pdf", "PDF content here");
        assertEquals(1L, p.getId());
        assertEquals("report.pdf", p.getName());
        assertEquals("PDF content here", p.getContent());
    }

    @Test
    void testBuilderPattern() {
        Pdf p = Pdf.builder()
                .id(2L)
                .name("invoice.pdf")
                .content("Invoice content")
                .build();

        assertEquals(2L, p.getId());
        assertEquals("invoice.pdf", p.getName());
        assertEquals("Invoice content", p.getContent());
    }

    @Test
    void testSetAndGetId() {
        pdf.setId(5L);
        assertEquals(5L, pdf.getId());
    }

    @Test
    void testSetAndGetName() {
        pdf.setName("document.pdf");
        assertEquals("document.pdf", pdf.getName());
    }

    @Test
    void testSetAndGetContent() {
        pdf.setContent("This is the PDF content.");
        assertEquals("This is the PDF content.", pdf.getContent());
    }

    @Test
    void testSetNullId() {
        pdf.setId(null);
        assertNull(pdf.getId());
    }

    @Test
    void testSetNullName() {
        pdf.setName(null);
        assertNull(pdf.getName());
    }

    @Test
    void testSetNullContent() {
        pdf.setContent(null);
        assertNull(pdf.getContent());
    }

    @Test
    void testEqualsAndHashCode() {
        Pdf p1 = Pdf.builder().id(1L).name("file.pdf").content("content").build();
        Pdf p2 = Pdf.builder().id(1L).name("file.pdf").content("content").build();
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testNotEquals() {
        Pdf p1 = Pdf.builder().id(1L).name("file1.pdf").build();
        Pdf p2 = Pdf.builder().id(2L).name("file2.pdf").build();
        assertNotEquals(p1, p2);
    }

    @Test
    void testToString() {
        pdf.setId(1L);
        pdf.setName("test.pdf");
        String str = pdf.toString();
        assertNotNull(str);
        assertTrue(str.contains("test.pdf"));
    }

    @Test
    void testNameWithExtension() {
        pdf.setName("report_2024.pdf");
        assertTrue(pdf.getName().endsWith(".pdf"));
    }

    @Test
    void testNameWithoutExtension() {
        pdf.setName("report");
        assertEquals("report", pdf.getName());
    }
}
