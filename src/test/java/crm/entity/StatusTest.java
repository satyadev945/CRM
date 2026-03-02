package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void status_shouldHaveFourValues() {
        Status[] values = Status.values();
        assertEquals(4, values.length);
    }

    @Test
    void status_shouldContainProposed() {
        Status status = Status.PROPOSED;
        assertNotNull(status);
        assertEquals("PROPOSED", status.name());
    }

    @Test
    void status_shouldContainNegotiated() {
        Status status = Status.NEGOTIATED;
        assertNotNull(status);
        assertEquals("NEGOTIATED", status.name());
    }

    @Test
    void status_shouldContainImplemented() {
        Status status = Status.IMPLEMENTED;
        assertNotNull(status);
        assertEquals("IMPLEMENTED", status.name());
    }

    @Test
    void status_shouldContainDone() {
        Status status = Status.DONE;
        assertNotNull(status);
        assertEquals("DONE", status.name());
    }

    @Test
    void all_shouldContainAllStatuses() {
        assertEquals(4, Status.ALL.length);
        assertEquals(Status.PROPOSED, Status.ALL[0]);
        assertEquals(Status.NEGOTIATED, Status.ALL[1]);
        assertEquals(Status.IMPLEMENTED, Status.ALL[2]);
        assertEquals(Status.DONE, Status.ALL[3]);
    }

    @Test
    void valueOf_withValidName_shouldReturnStatus() {
        Status status = Status.valueOf("PROPOSED");
        assertEquals(Status.PROPOSED, status);
    }

    @Test
    void valueOf_withInvalidName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Status.valueOf("INVALID");
        });
    }

    @Test
    void ordinal_shouldReturnCorrectOrder() {
        assertEquals(0, Status.PROPOSED.ordinal());
        assertEquals(1, Status.NEGOTIATED.ordinal());
        assertEquals(2, Status.IMPLEMENTED.ordinal());
        assertEquals(3, Status.DONE.ordinal());
    }

    @Test
    void compareTo_shouldCompareByOrdinal() {
        assertTrue(Status.PROPOSED.compareTo(Status.DONE) < 0);
        assertTrue(Status.DONE.compareTo(Status.PROPOSED) > 0);
        assertEquals(0, Status.PROPOSED.compareTo(Status.PROPOSED));
    }

    @Test
    void toString_shouldReturnName() {
        assertEquals("PROPOSED", Status.PROPOSED.toString());
        assertEquals("NEGOTIATED", Status.NEGOTIATED.toString());
        assertEquals("IMPLEMENTED", Status.IMPLEMENTED.toString());
        assertEquals("DONE", Status.DONE.toString());
    }
}
