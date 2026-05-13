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
    }

    @Test
    void testAllArgsConstructor() {
        Pdf p = new Pdf(1L, "report.pdf", "Some content");
        assertNotNull(p);
        assertEquals(1L, p.getId());
        assertEquals("report.pdf", p.getName());
        assertEquals("Some content", p.getContent());
    }

    @Test
    void testBuilderPattern() {
        Pdf p = Pdf.builder()
                .id(2L)
                .name("invoice.pdf")
                .content("Invoice content")
                .build();
        assertNotNull(p);
        assertEquals(2L, p.getId());
        assertEquals("invoice.pdf", p.getName());
        assertEquals("Invoice content", p.getContent());
    }

    @Test
    void testSetAndGetId() {
        pdf.setId(10L);
        assertEquals(10L, pdf.getId());
    }

    @Test
    void testSetAndGetName() {
        pdf.setName("document.pdf");
        assertEquals("document.pdf", pdf.getName());
    }

    @Test
    void testSetAndGetContent() {
        pdf.setContent("Hello World");
        assertEquals("Hello World", pdf.getContent());
    }

    @Test
    void testSetNameNull() {
        pdf.setName(null);
        assertNull(pdf.getName());
    }

    @Test
    void testSetContentNull() {
        pdf.setContent(null);
        assertNull(pdf.getContent());
    }

    @Test
    void testSetIdNull() {
        pdf.setId(null);
        assertNull(pdf.getId());
    }

    @Test
    void testEqualsAndHashCode() {
        Pdf p1 = Pdf.builder().id(1L).name("doc.pdf").content("content").build();
        Pdf p2 = Pdf.builder().id(1L).name("doc.pdf").content("content").build();
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testNotEquals() {
        Pdf p1 = Pdf.builder().id(1L).name("doc1.pdf").build();
        Pdf p2 = Pdf.builder().id(2L).name("doc2.pdf").build();
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
    void testSetNameWithExtension() {
        pdf.setName("report");
        assertEquals("report", pdf.getName());
    }

    @Test
    void testSetLargeContent() {
        String largeContent = "A".repeat(10000);
        pdf.setContent(largeContent);
        assertEquals(largeContent, pdf.getContent());
    }
}
