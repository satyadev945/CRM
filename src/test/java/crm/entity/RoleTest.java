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
    void testDefaultConstructor() {
        Role r = new Role();
        assertNotNull(r);
    }

    @Test
    void testSetAndGetId() {
        role.setId(1);
        assertEquals(1, role.getId());
    }

    @Test
    void testSetAndGetName() {
        role.setName("ROLE_ADMIN");
        assertEquals("ROLE_ADMIN", role.getName());
    }

    @Test
    void testSetNameNull() {
        role.setName(null);
        assertNull(role.getName());
    }

    @Test
    void testSetNameEmpty() {
        role.setName("");
        assertEquals("", role.getName());
    }

    @Test
    void testSetIdZero() {
        role.setId(0);
        assertEquals(0, role.getId());
    }

    @Test
    void testSetIdNegative() {
        role.setId(-1);
        assertEquals(-1, role.getId());
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
        role.setId(1);
        role.setName("ROLE_USER");
        String str = role.toString();
        assertNotNull(str);
        assertTrue(str.contains("ROLE_USER"));
    }

    @Test
    void testRoleNameRoleAdmin() {
        role.setName("ROLE_ADMIN");
        assertEquals("ROLE_ADMIN", role.getName());
    }

    @Test
    void testRoleNameRoleManager() {
        role.setName("ROLE_MANAGER");
        assertEquals("ROLE_MANAGER", role.getName());
    }
}
