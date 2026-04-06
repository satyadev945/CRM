package crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @Test
    void userRepository_shouldExtendJpaRepository() {
        // Assert
        assertTrue(JpaRepository.class.isAssignableFrom(UserRepository.class));
    }

    @Test
    void userRepository_shouldHaveRepositoryAnnotation() {
        // Assert
        assertTrue(UserRepository.class.isAnnotationPresent(org.springframework.stereotype.Repository.class));
    }

    @Test
    void userRepository_shouldHaveFindByUsernameMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(UserRepository.class.getMethod("findByUsername", String.class));
    }

    @Test
    void userRepository_shouldHaveFindAllByEnabledMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(UserRepository.class.getMethod("findAllByEnabled", int.class));
    }
}
