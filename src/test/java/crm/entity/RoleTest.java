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
    void setId_shouldSetId() {
        // Arrange
        int id = 1;

        // Act
        role.setId(id);

        // Assert
        assertEquals(id, role.getId());
    }

    @Test
    void setName_shouldSetName() {
        // Arrange
        String name = "ROLE_ADMIN";

        // Act
        role.setName(name);

        // Assert
        assertEquals(name, role.getName());
    }

    @Test
    void role_shouldHaveEntityAnnotation() {
        // Assert
        assertTrue(Role.class.isAnnotationPresent(javax.persistence.Entity.class));
    }

    @Test
    void role_shouldHaveTableAnnotation() {
        // Assert
        assertTrue(Role.class.isAnnotationPresent(javax.persistence.Table.class));
    }

    @Test
    void getId_shouldReturnZero_whenNotSet() {
        // Assert
        assertEquals(0, role.getId());
    }

    @Test
    void getName_shouldReturnNull_whenNotSet() {
        // Assert
        assertNull(role.getName());
    }

    @Test
    void equals_shouldReturnTrue_forSameObject() {
        // Assert
        assertEquals(role, role);
    }

    @Test
    void hashCode_shouldBeConsistent() {
        // Arrange
        int firstHashCode = role.hashCode();

        // Act
        int secondHashCode = role.hashCode();

        // Assert
        assertEquals(firstHashCode, secondHashCode);
    }

    @Test
    void toString_shouldNotReturnNull() {
        // Act
        String result = role.toString();

        // Assert
        assertNotNull(result);
    }

    @Test
    void setName_shouldAcceptDifferentRoleNames() {
        // Arrange & Act & Assert
        role.setName("ROLE_USER");
        assertEquals("ROLE_USER", role.getName());

        role.setName("ROLE_MANAGER");
        assertEquals("ROLE_MANAGER", role.getName());

        role.setName("ROLE_OWNER");
        assertEquals("ROLE_OWNER", role.getName());
    }
}
