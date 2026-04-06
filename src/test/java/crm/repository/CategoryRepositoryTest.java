package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

class CategoryRepositoryTest {

    @Test
    void categoryRepository_shouldExtendJpaRepository() {
        // Assert
        assertTrue(JpaRepository.class.isAssignableFrom(CategoryRepository.class));
    }

    @Test
    void categoryRepository_shouldHaveRepositoryAnnotation() {
        // Assert
        assertTrue(CategoryRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }

    @Test
    void categoryRepository_shouldHaveFindByNameMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(CategoryRepository.class.getMethod("findByName", String.class));
    }
}
