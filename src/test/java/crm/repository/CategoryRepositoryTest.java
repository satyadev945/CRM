package crm.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryRepositoryTest {

    @Test
    void categoryRepository_interfaceExists() {
        assertNotNull(CategoryRepository.class);
    }

    @Test
    void categoryRepository_hasFindByNameMethod() throws NoSuchMethodException {
        assertNotNull(CategoryRepository.class.getMethod("findByName", String.class));
    }
}
