package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testRoleCreation() {
        // Arrange
        Role role = new Role();
        int id = 1;
        String name = "ROLE_ADMIN";

        // Act
        role.setId(id);
        role.setName(name);

        // Assert
        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("ROLE_ADMIN");

        Role role2 = new Role();
        role2.setId(1);
        role2.setName("ROLE_ADMIN");

        Role role3 = new Role();
        role3.setId(2);
        role3.setName("ROLE_USER");

        // Assert
        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
        assertNotEquals(role1, role3);
        assertNotEquals(role1.hashCode(), role3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_ADMIN");

        // Act
        String toString = role.toString();

        // Assert
        // Since Lombok generates the toString, we just verify it contains the field values
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=ROLE_ADMIN"));
    }

    private void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Condition is not true");
        }
    }
}