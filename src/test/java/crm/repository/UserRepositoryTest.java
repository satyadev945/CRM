package crm.repository;

import crm.entity.Role;
import crm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User enabledUser;
    private User disabledUser;
    private Role role;

    @BeforeEach
    void setUp() {
        // Create a role
        role = new Role();
        role.setName("ROLE_ADMIN");
        role = entityManager.persist(role);

        // Create an enabled user
        enabledUser = new User();
        enabledUser.setUsername("enableduser");
        enabledUser.setEmail("enabled@example.com");
        enabledUser.setFirstName("John");
        enabledUser.setLastName("Doe");
        enabledUser.setPassword("password");
        enabledUser.setEnabled(1);
        enabledUser.setRole(role);
        enabledUser = entityManager.persist(enabledUser);

        // Create a disabled user
        disabledUser = new User();
        disabledUser.setUsername("disableduser");
        disabledUser.setEmail("disabled@example.com");
        disabledUser.setFirstName("Jane");
        disabledUser.setLastName("Smith");
        disabledUser.setPassword("password");
        disabledUser.setEnabled(0);
        disabledUser.setRole(role);
        disabledUser = entityManager.persist(disabledUser);

        entityManager.flush();
    }

    @Test
    void findByUsernameShouldReturnUser() {
        // Act
        User found = userRepository.findByUsername("enableduser");

        // Assert
        assertNotNull(found);
        assertEquals("enableduser", found.getUsername());
        assertEquals(enabledUser.getId(), found.getId());
    }

    @Test
    void findByUsernameShouldReturnNullWhenUserNotFound() {
        // Act
        User found = userRepository.findByUsername("nonexistentuser");

        // Assert
        assertNull(found);
    }

    @Test
    void findAllByEnabledShouldReturnOnlyEnabledUsers() {
        // Act
        List<User> enabledUsers = (List<User>) userRepository.findAllByEnabled(1);
        List<User> disabledUsers = (List<User>) userRepository.findAllByEnabled(0);

        // Assert
        assertFalse(enabledUsers.isEmpty());
        assertTrue(enabledUsers.contains(enabledUser));
        assertFalse(enabledUsers.contains(disabledUser));

        assertFalse(disabledUsers.isEmpty());
        assertFalse(disabledUsers.contains(enabledUser));
        assertTrue(disabledUsers.contains(disabledUser));
    }

    @Test
    void saveShouldPersistUser() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");
        newUser.setEnabled(1);
        newUser.setRole(role);

        // Act
        User saved = userRepository.save(newUser);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());

        // Verify it's in the database
        User found = entityManager.find(User.class, saved.getId());
        assertNotNull(found);
        assertEquals("newuser", found.getUsername());
    }

    @Test
    void deleteShouldRemoveUser() {
        // Arrange
        Long id = enabledUser.getId();

        // Act
        userRepository.deleteById(id);

        // Assert
        User found = entityManager.find(User.class, id);
        assertNull(found);
    }
}