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
    void testDefaultConstructor() {
        Category cat = new Category();
        assertNotNull(cat);
        assertNull(cat.getId());
        assertNull(cat.getName());
    }

    @Test
    void testSetAndGetId() {
        category.setId(1L);
        assertEquals(1L, category.getId());
    }

    @Test
    void testSetAndGetName() {
        category.setName("VIP");
        assertEquals("VIP", category.getName());
    }

    @Test
    void testSetIdNull() {
        category.setId(null);
        assertNull(category.getId());
    }

    @Test
    void testSetNameNull() {
        category.setName(null);
        assertNull(category.getName());
    }

    @Test
    void testSetNameEmpty() {
        category.setName("");
        assertEquals("", category.getName());
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
        cat2.setName("Regular");

        assertNotEquals(cat1, cat2);
    }

    @Test
    void testToString() {
        category.setId(1L);
        category.setName("VIP");
        String str = category.toString();
        assertNotNull(str);
        assertTrue(str.contains("VIP"));
    }

    @Test
    void testSetLargeId() {
        category.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, category.getId());
    }

    @Test
    void testSetNameWithSpecialChars() {
        category.setName("Category & Special #1");
        assertEquals("Category & Special #1", category.getName());
    }
}
