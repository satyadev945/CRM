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
        role.setName("ROLE_ADMIN");
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
        assertEquals("ROLE_ADMIN", role.getName());
    }

    @Test
    void testSetName() {
        role.setName("ROLE_USER");
        assertEquals("ROLE_USER", role.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        Role sameRole = new Role();
        sameRole.setId(1);
        sameRole.setName("ROLE_ADMIN");

        Role differentRole = new Role();
        differentRole.setId(2);
        differentRole.setName("ROLE_USER");

        assertEquals(role, sameRole);
        assertEquals(role.hashCode(), sameRole.hashCode());
        assertNotEquals(role, differentRole);
        assertNotEquals(role.hashCode(), differentRole.hashCode());
    }

    @Test
    void testToString() {
        String toString = role.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=ROLE_ADMIN"));
    }

    @Test
    void testNoArgsConstructor() {
        Role newRole = new Role();
        assertEquals(0, newRole.getId());
        assertNull(newRole.getName());
    }
}