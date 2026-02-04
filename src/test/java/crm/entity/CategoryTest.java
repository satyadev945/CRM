package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Retail");
    }

    @Test
    void testGetId() {
        assertEquals(1L, category.getId());
    }

    @Test
    void testSetId() {
        category.setId(2L);
        assertEquals(2L, category.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Retail", category.getName());
    }

    @Test
    void testSetName() {
        category.setName("Corporate");
        assertEquals("Corporate", category.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        Category sameCategory = new Category();
        sameCategory.setId(1L);
        sameCategory.setName("Retail");

        Category differentCategory = new Category();
        differentCategory.setId(2L);
        differentCategory.setName("Corporate");

        assertEquals(category, sameCategory);
        assertEquals(category.hashCode(), sameCategory.hashCode());
        assertNotEquals(category, differentCategory);
        assertNotEquals(category.hashCode(), differentCategory.hashCode());
    }

    @Test
    void testToString() {
        String toString = category.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Retail"));
    }

    @Test
    void testNoArgsConstructor() {
        Category newCategory = new Category();
        assertNull(newCategory.getId());
        assertNull(newCategory.getName());
    }
}