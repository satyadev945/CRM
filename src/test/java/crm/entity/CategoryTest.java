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
        category.setName("VIP");
    }

    @Test
    void testCategoryDefaultConstructor() {
        Category newCategory = new Category();
        assertNotNull(newCategory);
    }

    @Test
    void testGetId() {
        assertEquals(1L, category.getId());
    }

    @Test
    void testSetId() {
        category.setId(99L);
        assertEquals(99L, category.getId());
    }

    @Test
    void testGetName() {
        assertEquals("VIP", category.getName());
    }

    @Test
    void testSetName() {
        category.setName("Premium");
        assertEquals("Premium", category.getName());
    }

    @Test
    void testSetName_Standard() {
        category.setName("Standard");
        assertEquals("Standard", category.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("VIP");

        Category cat2 = new Category();
        cat2.setId(1L);
        cat2.setName("VIP");

        assertEquals(cat1, cat2);
        assertEquals(cat1.hashCode(), cat2.hashCode());
    }

    @Test
    void testNotEquals() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("VIP");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Standard");

        assertNotEquals(cat1, cat2);
    }

    @Test
    void testToString() {
        String toString = category.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("VIP"));
    }

    @Test
    void testCategoryWithNullName() {
        category.setName(null);
        assertNull(category.getName());
    }

    @Test
    void testCategoryIdNull() {
        Category newCategory = new Category();
        assertNull(newCategory.getId());
    }
}
