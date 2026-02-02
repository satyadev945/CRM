package crm.repository;

import crm.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByNameShouldReturnRole() {
        // Arrange
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        entityManager.persistAndFlush(role);

        // Act
        Role found = roleRepository.findByName("ROLE_ADMIN");

        // Assert
        assertNotNull(found);
        assertEquals("ROLE_ADMIN", found.getName());
    }

    @Test
    void findByNameShouldReturnNullWhenRoleNotFound() {
        // Act
        Role found = roleRepository.findByName("ROLE_NONEXISTENT");

        // Assert
        assertNull(found);
    }

    @Test
    void saveShouldPersistRole() {
        // Arrange
        Role role = new Role();
        role.setName("ROLE_NEW");

        // Act
        Role saved = roleRepository.save(role);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("ROLE_NEW", saved.getName());

        // Verify it's in the database
        Role found = entityManager.find(Role.class, saved.getId());
        assertNotNull(found);
        assertEquals("ROLE_NEW", found.getName());
    }

    @Test
    void deleteShouldRemoveRole() {
        // Arrange
        Role role = new Role();
        role.setName("ROLE_TO_DELETE");
        role = entityManager.persistAndFlush(role);
        int id = role.getId();

        // Act
        roleRepository.deleteById(id);

        // Assert
        Role found = entityManager.find(Role.class, id);
        assertNull(found);
    }
}