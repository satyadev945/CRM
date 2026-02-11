package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for UserRepository interface
 */
class UserRepositoryTest {

    @Test
    void testUserRepositoryInterface() {
        // Verify interface exists and extends JpaRepository
        assertTrue(UserRepository.class.isInterface());
        assertTrue(JpaRepository.class.isAssignableFrom(UserRepository.class));
    }

    @Test
    void testUserRepositoryMethods() {
        try {
            UserRepository.class.getMethod("findByUsername", String.class);
            UserRepository.class.getMethod("findAllByEnabled", int.class);
        } catch (NoSuchMethodException e) {
            fail("UserRepository interface missing expected methods");
        }
    }

    @Test
    void testRepositoryAnnotation() {
        assertTrue(UserRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }
}
