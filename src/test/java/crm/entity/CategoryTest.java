package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CategoryTest {

    @Test
    void testCategoryCreation() {
        // Arrange
        Category category = new Category();
        Long id = 1L;
        String name = "Test Category";

        // Act
        category.setId(id);
        category.setName(name);

        // Assert
        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Test Category");

        Category category2 = new Category();
        category2.setId(1L);
        category2.setName("Test Category");

        Category category3 = new Category();
        category3.setId(2L);
        category3.setName("Another Category");

        // Assert
        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
        assertNotEquals(category1, category3);
        assertNotEquals(category1.hashCode(), category3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        // Act
        String toString = category.toString();

        // Assert
        // Since Lombok generates the toString, we just verify it contains the field values
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test Category"));
    }

    private void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Condition is not true");
        }
    }
}