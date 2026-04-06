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
                .name("test-document")
                .content("This is test content")
                .build();
    }

    @Test
    void builder_shouldCreatePdfWithAllFields() {
        // Assert
        assertNotNull(pdf);
        assertEquals(1L, pdf.getId());
        assertEquals("test-document", pdf.getName());
        assertEquals("This is test content", pdf.getContent());
    }

    @Test
    void setId_shouldSetId() {
        // Arrange
        Long newId = 2L;

        // Act
        pdf.setId(newId);

        // Assert
        assertEquals(newId, pdf.getId());
    }

    @Test
    void setName_shouldSetName() {
        // Arrange
        String newName = "updated-document";

        // Act
        pdf.setName(newName);

        // Assert
        assertEquals(newName, pdf.getName());
    }

    @Test
    void setContent_shouldSetContent() {
        // Arrange
        String newContent = "Updated content";

        // Act
        pdf.setContent(newContent);

        // Assert
        assertEquals(newContent, pdf.getContent());
    }

    @Test
    void pdf_shouldHaveEntityAnnotation() {
        // Assert
        assertTrue(Pdf.class.isAnnotationPresent(javax.persistence.Entity.class));
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyPdf() {
        // Act
        Pdf emptyPdf = new Pdf();

        // Assert
        assertNotNull(emptyPdf);
        assertNull(emptyPdf.getId());
    }

    @Test
    void allArgsConstructor_shouldCreatePdfWithAllFields() {
        // Act
        Pdf newPdf = new Pdf(3L, "another-document", "Another content");

        // Assert
        assertNotNull(newPdf);
        assertEquals(3L, newPdf.getId());
        assertEquals("another-document", newPdf.getName());
        assertEquals("Another content", newPdf.getContent());
    }

    @Test
    void equals_shouldReturnTrue_forSameObject() {
        // Assert
        assertEquals(pdf, pdf);
    }

    @Test
    void hashCode_shouldBeConsistent() {
        // Arrange
        int firstHashCode = pdf.hashCode();

        // Act
        int secondHashCode = pdf.hashCode();

        // Assert
        assertEquals(firstHashCode, secondHashCode);
    }

    @Test
    void toString_shouldNotReturnNull() {
        // Act
        String result = pdf.toString();

        // Assert
        assertNotNull(result);
    }
}
