package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

class RoleRepositoryTest {

    @Test
    void roleRepository_shouldExtendJpaRepository() {
        // Assert
        assertTrue(JpaRepository.class.isAssignableFrom(RoleRepository.class));
    }

    @Test
    void roleRepository_shouldHaveRepositoryAnnotation() {
        // Assert
        assertTrue(RoleRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }

    @Test
    void roleRepository_shouldHaveFindByNameMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(RoleRepository.class.getMethod("findByName", String.class));
    }
}
