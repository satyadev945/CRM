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
    void testRoleDefaultConstructor() {
        Role newRole = new Role();
        assertNotNull(newRole);
    }

    @Test
    void testGetId() {
        assertEquals(1, role.getId());
    }

    @Test
    void testSetId() {
        role.setId(2);
        assertEquals(2, role.getId());
    }

    @Test
    void testGetName() {
        assertEquals("ROLE_USER", role.getName());
    }

    @Test
    void testSetName() {
        role.setName("ROLE_ADMIN");
        assertEquals("ROLE_ADMIN", role.getName());
    }

    @Test
    void testSetName_Manager() {
        role.setName("ROLE_MANAGER");
        assertEquals("ROLE_MANAGER", role.getName());
    }

    @Test
    void testSetName_Owner() {
        role.setName("ROLE_OWNER");
        assertEquals("ROLE_OWNER", role.getName());
    }

    @Test
    void testEqualsAndHashCode() {
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
    void testNotEquals() {
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("ROLE_ADMIN");

        assertNotEquals(role1, role2);
    }

    @Test
    void testToString() {
        String toString = role.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ROLE_USER"));
    }

    @Test
    void testRoleWithNullName() {
        role.setName(null);
        assertNull(role.getName());
    }

    @Test
    void testRoleIdZero() {
        Role newRole = new Role();
        assertEquals(0, newRole.getId());
    }
}
