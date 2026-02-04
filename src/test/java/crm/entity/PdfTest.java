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
        pdf.setName("Test PDF");
        pdf.setContent("This is the PDF content");
    }

    @Test
    void testGetId() {
        assertEquals(1L, pdf.getId());
    }

    @Test
    void testSetId() {
        pdf.setId(2L);
        assertEquals(2L, pdf.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Test PDF", pdf.getName());
    }

    @Test
    void testSetName() {
        pdf.setName("Updated PDF");
        assertEquals("Updated PDF", pdf.getName());
    }

    @Test
    void testGetContent() {
        assertEquals("This is the PDF content", pdf.getContent());
    }

    @Test
    void testSetContent() {
        pdf.setContent("Updated content");
        assertEquals("Updated content", pdf.getContent());
    }

    @Test
    void testBuilderPattern() {
        Pdf builtPdf = Pdf.builder()
                .id(2L)
                .name("Builder PDF")
                .content("Content created with builder")
                .build();

        assertEquals(2L, builtPdf.getId());
        assertEquals("Builder PDF", builtPdf.getName());
        assertEquals("Content created with builder", builtPdf.getContent());
    }

    @Test
    void testEqualsAndHashCode() {
        Pdf samePdf = new Pdf();
        samePdf.setId(1L);
        samePdf.setName("Test PDF");
        samePdf.setContent("This is the PDF content");

        Pdf differentPdf = new Pdf();
        differentPdf.setId(2L);
        differentPdf.setName("Different PDF");
        differentPdf.setContent("Different content");

        assertEquals(pdf, samePdf);
        assertEquals(pdf.hashCode(), samePdf.hashCode());
        assertNotEquals(pdf, differentPdf);
        assertNotEquals(pdf.hashCode(), differentPdf.hashCode());
    }

    @Test
    void testToString() {
        String toString = pdf.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test PDF"));
        assertTrue(toString.contains("content=This is the PDF content"));
    }

    @Test
    void testNoArgsConstructor() {
        Pdf emptyPdf = new Pdf();
        assertNull(emptyPdf.getId());
        assertNull(emptyPdf.getName());
        assertNull(emptyPdf.getContent());
    }

    @Test
    void testAllArgsConstructor() {
        Pdf constructedPdf = new Pdf(2L, "All Args PDF", "Content from all args constructor");

        assertEquals(2L, constructedPdf.getId());
        assertEquals("All Args PDF", constructedPdf.getName());
        assertEquals("Content from all args constructor", constructedPdf.getContent());
    }
}