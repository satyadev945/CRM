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
        assertEquals(0, r.getId());
        assertNull(r.getName());
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
    void testSetAndGetName_User() {
        role.setName("ROLE_USER");
        assertEquals("ROLE_USER", role.getName());
    }

    @Test
    void testSetAndGetName_Manager() {
        role.setName("ROLE_MANAGER");
        assertEquals("ROLE_MANAGER", role.getName());
    }

    @Test
    void testSetIdZero() {
        role.setId(0);
        assertEquals(0, role.getId());
    }

    @Test
    void testSetNameNull() {
        role.setName(null);
        assertNull(role.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        Role r1 = new Role();
        r1.setId(1);
        r1.setName("ROLE_ADMIN");

        Role r2 = new Role();
        r2.setId(1);
        r2.setName("ROLE_ADMIN");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testNotEquals() {
        Role r1 = new Role();
        r1.setId(1);
        r1.setName("ROLE_ADMIN");

        Role r2 = new Role();
        r2.setId(2);
        r2.setName("ROLE_USER");

        assertNotEquals(r1, r2);
    }

    @Test
    void testToString() {
        role.setId(1);
        role.setName("ROLE_ADMIN");
        String str = role.toString();
        assertNotNull(str);
        assertTrue(str.contains("ROLE_ADMIN"));
    }

    @Test
    void testSetLargeId() {
        role.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, role.getId());
    }
}
