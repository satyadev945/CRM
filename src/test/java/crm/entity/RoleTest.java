package crm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
    }

    @Test
    void testRoleCreation() {
        assertNotNull(role);
    }

    @Test
    void testGettersAndSetters() {
        role.setId(1);
        role.setName("ROLE_ADMIN");

        assertEquals(1, role.getId());
        assertEquals("ROLE_ADMIN", role.getName());
    }

    @Test
    void testIdSetting() {
        role.setId(5);
        assertEquals(5, role.getId());

        role.setId(0);
        assertEquals(0, role.getId());

        role.setId(-1);
        assertEquals(-1, role.getId());
    }

    @Test
    void testNameSetting() {
        role.setName("ROLE_USER");
        assertEquals("ROLE_USER", role.getName());

        role.setName("ROLE_MANAGER");
        assertEquals("ROLE_MANAGER", role.getName());

        role.setName("ROLE_OWNER");
        assertEquals("ROLE_OWNER", role.getName());
    }

    @Test
    void testNullName() {
        role.setName(null);
        assertNull(role.getName());
    }

    @Test
    void testEmptyName() {
        role.setName("");
        assertEquals("", role.getName());
    }

    @Test
    void testRoleNames() {
        String[] roleNames = {"ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER", "ROLE_OWNER"};

        for (String roleName : roleNames) {
            role.setName(roleName);
            assertEquals(roleName, role.getName());
        }
    }

    @Test
    void testEqualsAndHashCode() {
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ROLE_ADMIN");

        Role role2 = new Role();
        role2.setId(1);
        role2.setName("ROLE_ADMIN");

        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testNotEquals() {
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ROLE_ADMIN");

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("ROLE_USER");

        assertNotEquals(role1, role2);
    }

    @Test
    void testToString() {
        role.setId(1);
        role.setName("ROLE_ADMIN");

        String toString = role.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ROLE_ADMIN"));
    }

    @Test
    void testDefaultValues() {
        Role newRole = new Role();
        assertEquals(0, newRole.getId());
        assertNull(newRole.getName());
    }

    @Test
    void testMultipleRoleInstances() {
        Role adminRole = new Role();
        adminRole.setId(1);
        adminRole.setName("ROLE_ADMIN");

        Role userRole = new Role();
        userRole.setId(2);
        userRole.setName("ROLE_USER");

        Role managerRole = new Role();
        managerRole.setId(3);
        managerRole.setName("ROLE_MANAGER");

        assertNotEquals(adminRole, userRole);
        assertNotEquals(userRole, managerRole);
        assertNotEquals(adminRole, managerRole);
    }

    @Test
    void testRoleIdUniqueness() {
        role.setId(100);
        assertEquals(100, role.getId());

        Role anotherRole = new Role();
        anotherRole.setId(200);
        assertEquals(200, anotherRole.getId());

        assertNotEquals(role.getId(), anotherRole.getId());
    }
}
