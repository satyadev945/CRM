package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for RoleRepository interface
 */
class RoleRepositoryTest {

    @Test
    void testRoleRepositoryInterface() {
        // Verify interface exists and extends JpaRepository
        assertTrue(RoleRepository.class.isInterface());
        assertTrue(JpaRepository.class.isAssignableFrom(RoleRepository.class));
    }

    @Test
    void testRoleRepositoryMethods() {
        try {
            RoleRepository.class.getMethod("findByName", String.class);
        } catch (NoSuchMethodException e) {
            fail("RoleRepository interface missing expected methods");
        }
    }

    @Test
    void testRepositoryAnnotation() {
        assertTrue(RoleRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }
}
