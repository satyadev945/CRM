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
    void pdf_defaultConstructor_shouldCreateInstance() {
        assertNotNull(pdf);
    }

    @Test
    void pdf_allArgsConstructor_shouldCreateInstanceWithValues() {
        Pdf p = new Pdf(1L, "test.pdf", "Test content");

        assertNotNull(p);
        assertEquals(1L, p.getId());
        assertEquals("test.pdf", p.getName());
        assertEquals("Test content", p.getContent());
    }

    @Test
    void pdf_builder_shouldCreateInstance() {
        Pdf p = Pdf.builder()
                .id(1L)
                .name("document.pdf")
                .content("Document content")
                .build();

        assertNotNull(p);
        assertEquals(1L, p.getId());
        assertEquals("document.pdf", p.getName());
        assertEquals("Document content", p.getContent());
    }

    @Test
    void setId_shouldSetIdValue() {
        pdf.setId(10L);
        assertEquals(10L, pdf.getId());
    }

    @Test
    void setName_shouldSetNameValue() {
        pdf.setName("report.pdf");
        assertEquals("report.pdf", pdf.getName());
    }

    @Test
    void setContent_shouldSetContentValue() {
        pdf.setContent("PDF content here");
        assertEquals("PDF content here", pdf.getContent());
    }

    @Test
    void getId_withNullId_shouldReturnNull() {
        assertNull(pdf.getId());
    }

    @Test
    void getName_withNullName_shouldReturnNull() {
        assertNull(pdf.getName());
    }

    @Test
    void getContent_withNullContent_shouldReturnNull() {
        assertNull(pdf.getContent());
    }

    @Test
    void setName_withEmptyString_shouldSetEmptyString() {
        pdf.setName("");
        assertEquals("", pdf.getName());
    }

    @Test
    void setContent_withEmptyString_shouldSetEmptyString() {
        pdf.setContent("");
        assertEquals("", pdf.getContent());
    }

    @Test
    void equals_withSameValues_shouldReturnTrue() {
        Pdf p1 = new Pdf(1L, "test.pdf", "content");
        Pdf p2 = new Pdf(1L, "test.pdf", "content");

        assertEquals(p1, p2);
    }

    @Test
    void hashCode_withSameValues_shouldReturnSameHashCode() {
        Pdf p1 = new Pdf(1L, "test.pdf", "content");
        Pdf p2 = new Pdf(1L, "test.pdf", "content");

        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void toString_shouldContainFieldValues() {
        pdf.setId(1L);
        pdf.setName("test.pdf");
        pdf.setContent("content");

        String toString = pdf.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("test.pdf"));
        assertTrue(toString.contains("content"));
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        Pdf p = Pdf.builder()
                .name("partial.pdf")
                .build();

        assertNotNull(p);
        assertNull(p.getId());
        assertEquals("partial.pdf", p.getName());
        assertNull(p.getContent());
    }

    @Test
    void setName_withMaxLength_shouldSetValue() {
        String maxLengthName = "A".repeat(255);
        pdf.setName(maxLengthName);
        assertEquals(maxLengthName, pdf.getName());
    }

    @Test
    void setContent_withLongContent_shouldSetValue() {
        String longContent = "A".repeat(1000);
        pdf.setContent(longContent);
        assertEquals(longContent, pdf.getContent());
    }
}
