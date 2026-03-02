package crm.repository;

import crm.entity.Role;
import crm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void userRepository_shouldNotBeNull() {
        assertNotNull(userRepository);
    }

    @Test
    void save_shouldPersistUser() {
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setEnabled(1);
        user.setRole(role);

        User saved = userRepository.save(user);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
    }

    @Test
    void findByUsername_withExistingUsername_shouldReturnUser() {
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("adminuser");
        user.setEmail("admin@example.com");
        user.setPassword("password");
        user.setEnabled(1);
        user.setRole(role);
        userRepository.save(user);

        User found = userRepository.findByUsername("adminuser");

        assertNotNull(found);
        assertEquals("adminuser", found.getUsername());
    }

    @Test
    void findByUsername_withNonExistingUsername_shouldReturnNull() {
        User found = userRepository.findByUsername("nonexistent");
        assertNull(found);
    }

    @Test
    void findAllByEnabled_shouldReturnEnabledUsers() {
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

        User user1 = new User();
        user1.setUsername("enabled1");
        user1.setEmail("enabled1@example.com");
        user1.setPassword("password");
        user1.setEnabled(1);
        user1.setRole(role);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("disabled1");
        user2.setEmail("disabled1@example.com");
        user2.setPassword("password");
        user2.setEnabled(0);
        user2.setRole(role);
        userRepository.save(user2);

        Iterable<User> enabledUsers = userRepository.findAllByEnabled(1);

        assertNotNull(enabledUsers);
        assertTrue(enabledUsers.iterator().hasNext());
    }

    @Test
    void delete_shouldRemoveUser() {
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("todelete");
        user.setEmail("todelete@example.com");
        user.setPassword("password");
        user.setEnabled(1);
        user.setRole(role);
        User saved = userRepository.save(user);

        userRepository.delete(saved);

        assertFalse(userRepository.findById(saved.getId()).isPresent());
    }
}
