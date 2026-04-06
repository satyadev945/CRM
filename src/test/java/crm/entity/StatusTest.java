package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void status_shouldHaveProposedValue() {
        // Act
        Status status = Status.PROPOSED;

        // Assert
        assertNotNull(status);
        assertEquals("PROPOSED", status.name());
    }

    @Test
    void status_shouldHaveNegotiatedValue() {
        // Act
        Status status = Status.NEGOTIATED;

        // Assert
        assertNotNull(status);
        assertEquals("NEGOTIATED", status.name());
    }

    @Test
    void status_shouldHaveImplementedValue() {
        // Act
        Status status = Status.IMPLEMENTED;

        // Assert
        assertNotNull(status);
        assertEquals("IMPLEMENTED", status.name());
    }

    @Test
    void status_shouldHaveDoneValue() {
        // Act
        Status status = Status.DONE;

        // Assert
        assertNotNull(status);
        assertEquals("DONE", status.name());
    }

    @Test
    void status_allArray_shouldContainAllValues() {
        // Assert
        assertNotNull(Status.ALL);
        assertEquals(4, Status.ALL.length);
        assertEquals(Status.PROPOSED, Status.ALL[0]);
        assertEquals(Status.NEGOTIATED, Status.ALL[1]);
        assertEquals(Status.IMPLEMENTED, Status.ALL[2]);
        assertEquals(Status.DONE, Status.ALL[3]);
    }

    @Test
    void valueOf_shouldReturnCorrectStatus() {
        // Act & Assert
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));
        assertEquals(Status.NEGOTIATED, Status.valueOf("NEGOTIATED"));
        assertEquals(Status.IMPLEMENTED, Status.valueOf("IMPLEMENTED"));
        assertEquals(Status.DONE, Status.valueOf("DONE"));
    }

    @Test
    void values_shouldReturnAllStatuses() {
        // Act
        Status[] statuses = Status.values();

        // Assert
        assertNotNull(statuses);
        assertEquals(4, statuses.length);
    }

    @Test
    void status_shouldBeEnum() {
        // Assert
        assertTrue(Status.class.isEnum());
    }

    @Test
    void valueOf_shouldThrowException_forInvalidValue() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            Status.valueOf("INVALID");
        });
    }
}
