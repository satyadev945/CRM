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
    void testCategoryCreation() {
        assertNotNull(category);
    }

    @Test
    void testGettersAndSetters() {
        category.setId(1L);
        category.setName("VIP");

        assertEquals(1L, category.getId());
        assertEquals("VIP", category.getName());
    }

    @Test
    void testIdSetting() {
        category.setId(100L);
        assertEquals(100L, category.getId());

        category.setId(0L);
        assertEquals(0L, category.getId());
    }

    @Test
    void testNameSetting() {
        category.setName("Premium");
        assertEquals("Premium", category.getName());

        category.setName("Standard");
        assertEquals("Standard", category.getName());

        category.setName("VIP");
        assertEquals("VIP", category.getName());
    }

    @Test
    void testNullName() {
        category.setName(null);
        assertNull(category.getName());
    }

    @Test
    void testEmptyName() {
        category.setName("");
        assertEquals("", category.getName());
    }

    @Test
    void testNullId() {
        category.setId(null);
        assertNull(category.getId());
    }

    @Test
    void testEqualsAndHashCode() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("VIP");

        Category category2 = new Category();
        category2.setId(1L);
        category2.setName("VIP");

        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    void testNotEquals() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("VIP");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Premium");

        assertNotEquals(category1, category2);
    }

    @Test
    void testToString() {
        category.setId(1L);
        category.setName("VIP");

        String toString = category.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("VIP"));
    }

    @Test
    void testDefaultValues() {
        Category newCategory = new Category();
        assertNull(newCategory.getId());
        assertNull(newCategory.getName());
    }

    @Test
    void testMultipleCategoryInstances() {
        Category vipCategory = new Category();
        vipCategory.setId(1L);
        vipCategory.setName("VIP");

        Category premiumCategory = new Category();
        premiumCategory.setId(2L);
        premiumCategory.setName("Premium");

        Category standardCategory = new Category();
        standardCategory.setId(3L);
        standardCategory.setName("Standard");

        assertNotEquals(vipCategory, premiumCategory);
        assertNotEquals(premiumCategory, standardCategory);
        assertNotEquals(vipCategory, standardCategory);
    }

    @Test
    void testCategoryIdUniqueness() {
        category.setId(100L);
        assertEquals(100L, category.getId());

        Category anotherCategory = new Category();
        anotherCategory.setId(200L);
        assertEquals(200L, anotherCategory.getId());

        assertNotEquals(category.getId(), anotherCategory.getId());
    }

    @Test
    void testCategoryNameTypes() {
        String[] categoryNames = {"VIP", "Premium", "Standard", "Bronze", "Silver", "Gold", "Platinum"};

        for (String name : categoryNames) {
            category.setName(name);
            assertEquals(name, category.getName());
        }
    }

    @Test
    void testLongCategoryName() {
        String longName = "Very Long Category Name That Might Be Used In Real Applications";
        category.setName(longName);
        assertEquals(longName, category.getName());
    }

    @Test
    void testSpecialCharactersInName() {
        String specialName = "VIP-Premium_2024";
        category.setName(specialName);
        assertEquals(specialName, category.getName());
    }
}
