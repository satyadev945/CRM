package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for CategoryRepository interface
 */
class CategoryRepositoryTest {

    @Test
    void testCategoryRepositoryInterface() {
        // Verify interface exists and extends JpaRepository
        assertTrue(CategoryRepository.class.isInterface());
        assertTrue(JpaRepository.class.isAssignableFrom(CategoryRepository.class));
    }

    @Test
    void testCategoryRepositoryMethods() {
        try {
            CategoryRepository.class.getMethod("findByName", String.class);
        } catch (NoSuchMethodException e) {
            fail("CategoryRepository interface missing expected methods");
        }
    }

    @Test
    void testRepositoryAnnotation() {
        assertTrue(CategoryRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }
}
