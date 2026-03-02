package crm.repository;

import crm.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void categoryRepository_shouldNotBeNull() {
        assertNotNull(categoryRepository);
    }

    @Test
    void save_shouldPersistCategory() {
        Category category = new Category();
        category.setName("Electronics");

        Category saved = categoryRepository.save(category);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Electronics", saved.getName());
    }

    @Test
    void findByName_withExistingName_shouldReturnCategory() {
        Category category = new Category();
        category.setName("Books");
        categoryRepository.save(category);

        Category found = categoryRepository.findByName("Books");

        assertNotNull(found);
        assertEquals("Books", found.getName());
    }

    @Test
    void findByName_withNonExistingName_shouldReturnNull() {
        Category found = categoryRepository.findByName("NonExistent");
        assertNull(found);
    }

    @Test
    void findById_withExistingId_shouldReturnCategory() {
        Category category = new Category();
        category.setName("Furniture");
        Category saved = categoryRepository.save(category);

        Category found = categoryRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Furniture", found.getName());
    }

    @Test
    void delete_shouldRemoveCategory() {
        Category category = new Category();
        category.setName("ToDelete");
        Category saved = categoryRepository.save(category);

        categoryRepository.delete(saved);

        assertFalse(categoryRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void findAll_shouldReturnAllCategories() {
        Category cat1 = new Category();
        cat1.setName("Category1");
        categoryRepository.save(cat1);

        Category cat2 = new Category();
        cat2.setName("Category2");
        categoryRepository.save(cat2);

        assertTrue(categoryRepository.findAll().size() >= 2);
    }

    @Test
    void count_shouldReturnNumberOfCategories() {
        long initialCount = categoryRepository.count();

        Category category = new Category();
        category.setName("NewCategory");
        categoryRepository.save(category);

        assertEquals(initialCount + 1, categoryRepository.count());
    }
}
