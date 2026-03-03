package crm.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleRepositoryTest {

    @Test
    void roleRepository_interfaceExists() {
        assertNotNull(RoleRepository.class);
    }

    @Test
    void roleRepository_hasFindByNameMethod() throws NoSuchMethodException {
        assertNotNull(RoleRepository.class.getMethod("findByName", String.class));
    }
}
