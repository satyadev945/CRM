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
    void testPdfBuilder() {
        Pdf builtPdf = Pdf.builder()
                .id(1L)
                .name("TestDocument")
                .content("PDF Content Here")
                .build();

        assertNotNull(builtPdf);
        assertEquals(1L, builtPdf.getId());
        assertEquals("TestDocument", builtPdf.getName());
        assertEquals("PDF Content Here", builtPdf.getContent());
    }

    @Test
    void testNoArgsConstructor() {
        Pdf newPdf = new Pdf();
        assertNotNull(newPdf);
    }

    @Test
    void testAllArgsConstructor() {
        Pdf newPdf = new Pdf(1L, "TestDocument", "PDF Content Here");

        assertNotNull(newPdf);
        assertEquals(1L, newPdf.getId());
        assertEquals("TestDocument", newPdf.getName());
        assertEquals("PDF Content Here", newPdf.getContent());
    }

    @Test
    void testGettersAndSetters() {
        pdf.setId(1L);
        pdf.setName("TestDocument");
        pdf.setContent("PDF Content Here");

        assertEquals(1L, pdf.getId());
        assertEquals("TestDocument", pdf.getName());
        assertEquals("PDF Content Here", pdf.getContent());
    }

    @Test
    void testIdSetting() {
        pdf.setId(100L);
        assertEquals(100L, pdf.getId());

        pdf.setId(0L);
        assertEquals(0L, pdf.getId());
    }

    @Test
    void testNameSetting() {
        pdf.setName("Invoice_2024");
        assertEquals("Invoice_2024", pdf.getName());

        pdf.setName("Report_Q1");
        assertEquals("Report_Q1", pdf.getName());
    }

    @Test
    void testContentSetting() {
        String content = "This is the PDF content";
        pdf.setContent(content);
        assertEquals(content, pdf.getContent());
    }

    @Test
    void testNullName() {
        pdf.setName(null);
        assertNull(pdf.getName());
    }

    @Test
    void testNullContent() {
        pdf.setContent(null);
        assertNull(pdf.getContent());
    }

    @Test
    void testNullId() {
        pdf.setId(null);
        assertNull(pdf.getId());
    }

    @Test
    void testEmptyName() {
        pdf.setName("");
        assertEquals("", pdf.getName());
    }

    @Test
    void testEmptyContent() {
        pdf.setContent("");
        assertEquals("", pdf.getContent());
    }

    @Test
    void testEqualsAndHashCode() {
        Pdf pdf1 = Pdf.builder()
                .id(1L)
                .name("Document")
                .content("Content")
                .build();

        Pdf pdf2 = Pdf.builder()
                .id(1L)
                .name("Document")
                .content("Content")
                .build();

        assertEquals(pdf1, pdf2);
        assertEquals(pdf1.hashCode(), pdf2.hashCode());
    }

    @Test
    void testNotEquals() {
        Pdf pdf1 = Pdf.builder()
                .id(1L)
                .name("Document1")
                .build();

        Pdf pdf2 = Pdf.builder()
                .id(2L)
                .name("Document2")
                .build();

        assertNotEquals(pdf1, pdf2);
    }

    @Test
    void testToString() {
        pdf.setId(1L);
        pdf.setName("TestDocument");
        pdf.setContent("Content");

        String toString = pdf.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("TestDocument"));
    }

    @Test
    void testLongContent() {
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longContent.append("Content line ").append(i).append(". ");
        }

        pdf.setContent(longContent.toString());
        assertEquals(longContent.toString(), pdf.getContent());
        assertTrue(pdf.getContent().length() > 1000);
    }

    @Test
    void testSpecialCharactersInName() {
        String specialName = "Document_2024-Q1_Final.pdf";
        pdf.setName(specialName);
        assertEquals(specialName, pdf.getName());
    }

    @Test
    void testMinimumNameLength() {
        String shortName = "AB";
        pdf.setName(shortName);
        assertEquals(shortName, pdf.getName());
        assertTrue(pdf.getName().length() >= 2);
    }

    @Test
    void testContentIsTransient() {
        // The content field is marked as @Transient, meaning it won't be persisted
        pdf.setContent("Transient content");
        assertEquals("Transient content", pdf.getContent());
    }

    @Test
    void testMultiplePdfInstances() {
        Pdf pdf1 = Pdf.builder()
                .id(1L)
                .name("Document1")
                .content("Content1")
                .build();

        Pdf pdf2 = Pdf.builder()
                .id(2L)
                .name("Document2")
                .content("Content2")
                .build();

        assertNotEquals(pdf1, pdf2);
        assertNotEquals(pdf1.getId(), pdf2.getId());
        assertNotEquals(pdf1.getName(), pdf2.getName());
        assertNotEquals(pdf1.getContent(), pdf2.getContent());
    }

    @Test
    void testPdfWithOnlyName() {
        pdf.setName("OnlyNameDocument");
        assertEquals("OnlyNameDocument", pdf.getName());
        assertNull(pdf.getContent());
        assertNull(pdf.getId());
    }
}
