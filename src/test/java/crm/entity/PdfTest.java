package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfTest {

    @Test
    void testPdfCreation() {
        // Arrange
        Pdf pdf = new Pdf();
        Long id = 1L;
        String name = "Test PDF";
        String content = "PDF content";

        // Act
        pdf.setId(id);
        pdf.setName(name);
        pdf.setContent(content);

        // Assert
        assertEquals(id, pdf.getId());
        assertEquals(name, pdf.getName());
        assertEquals(content, pdf.getContent());
    }

    @Test
    void testBuilderPattern() {
        // Arrange
        Long id = 1L;
        String name = "Test PDF";
        String content = "PDF content";

        // Act
        Pdf pdf = Pdf.builder()
                .id(id)
                .name(name)
                .content(content)
                .build();

        // Assert
        assertEquals(id, pdf.getId());
        assertEquals(name, pdf.getName());
        assertEquals(content, pdf.getContent());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String name = "Test PDF";
        String content = "PDF content";

        // Act
        Pdf pdf = new Pdf(id, name, content);

        // Assert
        assertEquals(id, pdf.getId());
        assertEquals(name, pdf.getName());
        assertEquals(content, pdf.getContent());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Pdf pdf1 = Pdf.builder()
                .id(1L)
                .name("Test PDF")
                .content("Content")
                .build();

        Pdf pdf2 = Pdf.builder()
                .id(1L)
                .name("Test PDF")
                .content("Content")
                .build();

        Pdf pdf3 = Pdf.builder()
                .id(2L)
                .name("Another PDF")
                .content("Different content")
                .build();

        // Assert
        assertEquals(pdf1, pdf2);
        assertEquals(pdf1.hashCode(), pdf2.hashCode());
        assertNotEquals(pdf1, pdf3);
        assertNotEquals(pdf1.hashCode(), pdf3.hashCode());
    }
}