package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    void setId_shouldSetId() {
        // Arrange
        Long id = 1L;

        // Act
        category.setId(id);

        // Assert
        assertEquals(id, category.getId());
    }

    @Test
    void setName_shouldSetName() {
        // Arrange
        String name = "Technology";

        // Act
        category.setName(name);

        // Assert
        assertEquals(name, category.getName());
    }

    @Test
    void category_shouldHaveEntityAnnotation() {
        // Assert
        assertTrue(Category.class.isAnnotationPresent(javax.persistence.Entity.class));
    }

    @Test
    void category_shouldHaveTableAnnotation() {
        // Assert
        assertTrue(Category.class.isAnnotationPresent(javax.persistence.Table.class));
    }

    @Test
    void getId_shouldReturnNull_whenNotSet() {
        // Assert
        assertNull(category.getId());
    }

    @Test
    void getName_shouldReturnNull_whenNotSet() {
        // Assert
        assertNull(category.getName());
    }

    @Test
    void equals_shouldReturnTrue_forSameObject() {
        // Assert
        assertEquals(category, category);
    }

    @Test
    void hashCode_shouldBeConsistent() {
        // Arrange
        int firstHashCode = category.hashCode();

        // Act
        int secondHashCode = category.hashCode();

        // Assert
        assertEquals(firstHashCode, secondHashCode);
    }

    @Test
    void toString_shouldNotReturnNull() {
        // Act
        String result = category.toString();

        // Assert
        assertNotNull(result);
    }
}
