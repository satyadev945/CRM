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
    void setAndGetId_shouldWorkCorrectly() {
        Long id = 1L;
        category.setId(id);
        
        assertEquals(id, category.getId());
    }

    @Test
    void setAndGetName_shouldWorkCorrectly() {
        String name = "Technology";
        category.setName(name);
        
        assertEquals(name, category.getName());
    }

    @Test
    void category_shouldBeInstantiable() {
        assertNotNull(category);
    }

    @Test
    void category_withNullValues_shouldHandleGracefully() {
        category.setId(null);
        category.setName(null);
        
        assertNull(category.getId());
        assertNull(category.getName());
    }

    @Test
    void category_equalsAndHashCode_shouldWorkCorrectly() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Tech");

        Category category2 = new Category();
        category2.setId(1L);
        category2.setName("Tech");

        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    void category_toString_shouldReturnString() {
        category.setId(1L);
        category.setName("Tech");
        
        String result = category.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Tech"));
    }
}
