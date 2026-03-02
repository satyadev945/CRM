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
    void role_defaultConstructor_shouldCreateInstance() {
        assertNotNull(role);
    }

    @Test
    void role_allArgsConstructor_shouldCreateInstanceWithValues() {
        Role r = new Role(1L, "ADMIN");

        assertNotNull(r);
        assertEquals(1L, r.getId());
        assertEquals("ADMIN", r.getName());
    }

    @Test
    void role_builder_shouldCreateInstance() {
        Role r = Role.builder()
                .id(1L)
                .name("USER")
                .build();

        assertNotNull(r);
        assertEquals(1L, r.getId());
        assertEquals("USER", r.getName());
    }

    @Test
    void setId_shouldSetIdValue() {
        role.setId(10L);
        assertEquals(10L, role.getId());
    }

    @Test
    void setName_shouldSetNameValue() {
        role.setName("MANAGER");
        assertEquals("MANAGER", role.getName());
    }

    @Test
    void getId_withNullId_shouldReturnNull() {
        assertNull(role.getId());
    }

    @Test
    void getName_withNullName_shouldReturnNull() {
        assertNull(role.getName());
    }

    @Test
    void setName_withEmptyString_shouldSetEmptyString() {
        role.setName("");
        assertEquals("", role.getName());
    }

    @Test
    void equals_withSameValues_shouldReturnTrue() {
        Role r1 = new Role(1L, "ADMIN");
        Role r2 = new Role(1L, "ADMIN");

        assertEquals(r1, r2);
    }

    @Test
    void hashCode_withSameValues_shouldReturnSameHashCode() {
        Role r1 = new Role(1L, "ADMIN");
        Role r2 = new Role(1L, "ADMIN");

        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toString_shouldContainFieldValues() {
        role.setId(1L);
        role.setName("ADMIN");

        String toString = role.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("ADMIN"));
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        Role r = Role.builder()
                .name("OWNER")
                .build();

        assertNotNull(r);
        assertNull(r.getId());
        assertEquals("OWNER", r.getName());
    }

    @Test
    void setName_withMaxLength_shouldSetValue() {
        String maxLengthName = "A".repeat(50);
        role.setName(maxLengthName);
        assertEquals(maxLengthName, role.getName());
    }
}
