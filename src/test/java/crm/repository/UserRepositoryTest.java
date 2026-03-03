package crm.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @Test
    void userRepository_interfaceExists() {
        assertNotNull(UserRepository.class);
    }

    @Test
    void userRepository_hasFindByUsernameMethod() throws NoSuchMethodException {
        assertNotNull(UserRepository.class.getMethod("findByUsername", String.class));
    }

    @Test
    void userRepository_hasFindAllByEnabledMethod() throws NoSuchMethodException {
        assertNotNull(UserRepository.class.getMethod("findAllByEnabled", int.class));
    }
}
