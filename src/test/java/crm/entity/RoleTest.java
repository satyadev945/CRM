package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");
    }

    @Test
    void setAndGetId_shouldWorkCorrectly() {
        role.setId(2);
        assertEquals(2, role.getId());
    }

    @Test
    void setAndGetName_shouldWorkCorrectly() {
        role.setName("ROLE_ADMIN");
        assertEquals("ROLE_ADMIN", role.getName());
    }

    @Test
    void role_shouldBeInstantiable() {
        assertNotNull(role);
    }

    @Test
    void role_withNullValues_shouldHandleGracefully() {
        Role nullRole = new Role();
        nullRole.setId(0);
        nullRole.setName(null);
        
        assertEquals(0, nullRole.getId());
        assertNull(nullRole.getName());
    }

    @Test
    void role_withDifferentRoleNames_shouldHandleCorrectly() {
        String[] roleNames = {"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_OWNER"};
        
        for (String roleName : roleNames) {
            role.setName(roleName);
            assertEquals(roleName, role.getName());
        }
    }

    @Test
    void role_equalsAndHashCode_shouldWorkCorrectly() {
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(1);
        role2.setName("ROLE_USER");

        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void role_toString_shouldReturnString() {
        String result = role.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("ROLE_USER"));
    }
}
