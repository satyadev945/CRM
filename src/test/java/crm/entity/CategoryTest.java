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
    void category_defaultConstructor_shouldCreateInstance() {
        assertNotNull(category);
    }

    @Test
    void category_allArgsConstructor_shouldCreateInstanceWithValues() {
        Category cat = new Category(1L, "Test Category");

        assertNotNull(cat);
        assertEquals(1L, cat.getId());
        assertEquals("Test Category", cat.getName());
    }

    @Test
    void category_builder_shouldCreateInstance() {
        Category cat = Category.builder()
                .id(1L)
                .name("Builder Category")
                .build();

        assertNotNull(cat);
        assertEquals(1L, cat.getId());
        assertEquals("Builder Category", cat.getName());
    }

    @Test
    void setId_shouldSetIdValue() {
        category.setId(10L);
        assertEquals(10L, category.getId());
    }

    @Test
    void setName_shouldSetNameValue() {
        category.setName("Electronics");
        assertEquals("Electronics", category.getName());
    }

    @Test
    void getId_withNullId_shouldReturnNull() {
        assertNull(category.getId());
    }

    @Test
    void getName_withNullName_shouldReturnNull() {
        assertNull(category.getName());
    }

    @Test
    void setName_withEmptyString_shouldSetEmptyString() {
        category.setName("");
        assertEquals("", category.getName());
    }

    @Test
    void setName_withLongString_shouldSetLongString() {
        String longName = "A".repeat(100);
        category.setName(longName);
        assertEquals(longName, category.getName());
    }

    @Test
    void equals_withSameValues_shouldReturnTrue() {
        Category cat1 = new Category(1L, "Test");
        Category cat2 = new Category(1L, "Test");

        assertEquals(cat1, cat2);
    }

    @Test
    void hashCode_withSameValues_shouldReturnSameHashCode() {
        Category cat1 = new Category(1L, "Test");
        Category cat2 = new Category(1L, "Test");

        assertEquals(cat1.hashCode(), cat2.hashCode());
    }

    @Test
    void toString_shouldContainFieldValues() {
        category.setId(1L);
        category.setName("Test Category");

        String toString = category.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("Test Category"));
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        Category cat = Category.builder()
                .name("Partial Category")
                .build();

        assertNotNull(cat);
        assertNull(cat.getId());
        assertEquals("Partial Category", cat.getName());
    }

    @Test
    void setId_withZero_shouldSetZero() {
        category.setId(0L);
        assertEquals(0L, category.getId());
    }

    @Test
    void setId_withNegativeValue_shouldSetNegativeValue() {
        category.setId(-1L);
        assertEquals(-1L, category.getId());
    }
}
