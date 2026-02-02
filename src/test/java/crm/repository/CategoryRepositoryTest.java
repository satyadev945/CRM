package crm.repository;

import crm.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByNameShouldReturnCategory() {
        // Arrange
        Category category = new Category();
        category.setName("Test Category");
        entityManager.persistAndFlush(category);

        // Act
        Category found = categoryRepository.findByName("Test Category");

        // Assert
        assertNotNull(found);
        assertEquals("Test Category", found.getName());
    }

    @Test
    void findByNameShouldReturnNullWhenCategoryNotFound() {
        // Act
        Category found = categoryRepository.findByName("Nonexistent Category");

        // Assert
        assertNull(found);
    }

    @Test
    void saveShouldPersistCategory() {
        // Arrange
        Category category = new Category();
        category.setName("New Category");

        // Act
        Category saved = categoryRepository.save(category);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("New Category", saved.getName());

        // Verify it's in the database
        Category found = entityManager.find(Category.class, saved.getId());
        assertNotNull(found);
        assertEquals("New Category", found.getName());
    }

    @Test
    void deleteShouldRemoveCategory() {
        // Arrange
        Category category = new Category();
        category.setName("Category to Delete");
        category = entityManager.persistAndFlush(category);
        Long id = category.getId();

        // Act
        categoryRepository.deleteById(id);

        // Assert
        Category found = entityManager.find(Category.class, id);
        assertNull(found);
    }
}