package crm.repository;

import crm.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void roleRepository_shouldNotBeNull() {
        assertNotNull(roleRepository);
    }

    @Test
    void save_shouldPersistRole() {
        Role role = new Role();
        role.setName("ADMIN");

        Role saved = roleRepository.save(role);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("ADMIN", saved.getName());
    }

    @Test
    void findByName_withExistingName_shouldReturnRole() {
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

        Role found = roleRepository.findByName("USER");

        assertNotNull(found);
        assertEquals("USER", found.getName());
    }

    @Test
    void findByName_withNonExistingName_shouldReturnNull() {
        Role found = roleRepository.findByName("NONEXISTENT");
        assertNull(found);
    }

    @Test
    void findById_withExistingId_shouldReturnRole() {
        Role role = new Role();
        role.setName("MANAGER");
        Role saved = roleRepository.save(role);

        Role found = roleRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("MANAGER", found.getName());
    }

    @Test
    void delete_shouldRemoveRole() {
        Role role = new Role();
        role.setName("TODELETE");
        Role saved = roleRepository.save(role);

        roleRepository.delete(saved);

        assertFalse(roleRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void findAll_shouldReturnAllRoles() {
        Role role1 = new Role();
        role1.setName("ROLE1");
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setName("ROLE2");
        roleRepository.save(role2);

        assertTrue(roleRepository.findAll().size() >= 2);
    }

    @Test
    void count_shouldReturnNumberOfRoles() {
        long initialCount = roleRepository.count();

        Role role = new Role();
        role.setName("NEWROLE");
        roleRepository.save(role);

        assertEquals(initialCount + 1, roleRepository.count());
    }
}
