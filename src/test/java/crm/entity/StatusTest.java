package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void status_shouldHaveAllValues() {
        Status[] statuses = Status.values();
        
        assertEquals(4, statuses.length);
        assertTrue(containsStatus(statuses, Status.PROPOSED));
        assertTrue(containsStatus(statuses, Status.NEGOTIATED));
        assertTrue(containsStatus(statuses, Status.IMPLEMENTED));
        assertTrue(containsStatus(statuses, Status.DONE));
    }

    @Test
    void status_ALL_shouldContainAllStatuses() {
        assertEquals(4, Status.ALL.length);
        assertEquals(Status.PROPOSED, Status.ALL[0]);
        assertEquals(Status.NEGOTIATED, Status.ALL[1]);
        assertEquals(Status.IMPLEMENTED, Status.ALL[2]);
        assertEquals(Status.DONE, Status.ALL[3]);
    }

    @Test
    void status_valueOf_shouldReturnCorrectStatus() {
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));
        assertEquals(Status.NEGOTIATED, Status.valueOf("NEGOTIATED"));
        assertEquals(Status.IMPLEMENTED, Status.valueOf("IMPLEMENTED"));
        assertEquals(Status.DONE, Status.valueOf("DONE"));
    }

    @Test
    void status_valueOf_withInvalidValue_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> Status.valueOf("INVALID"));
    }

    @Test
    void status_name_shouldReturnCorrectName() {
        assertEquals("PROPOSED", Status.PROPOSED.name());
        assertEquals("NEGOTIATED", Status.NEGOTIATED.name());
        assertEquals("IMPLEMENTED", Status.IMPLEMENTED.name());
        assertEquals("DONE", Status.DONE.name());
    }

    @Test
    void status_ordinal_shouldReturnCorrectOrder() {
        assertEquals(0, Status.PROPOSED.ordinal());
        assertEquals(1, Status.NEGOTIATED.ordinal());
        assertEquals(2, Status.IMPLEMENTED.ordinal());
        assertEquals(3, Status.DONE.ordinal());
    }

    @Test
    void status_toString_shouldReturnName() {
        assertEquals("PROPOSED", Status.PROPOSED.toString());
        assertEquals("NEGOTIATED", Status.NEGOTIATED.toString());
        assertEquals("IMPLEMENTED", Status.IMPLEMENTED.toString());
        assertEquals("DONE", Status.DONE.toString());
    }

    @Test
    void status_compareTo_shouldWorkCorrectly() {
        assertTrue(Status.PROPOSED.compareTo(Status.DONE) < 0);
        assertTrue(Status.DONE.compareTo(Status.PROPOSED) > 0);
        assertEquals(0, Status.PROPOSED.compareTo(Status.PROPOSED));
    }

    private boolean containsStatus(Status[] statuses, Status status) {
        for (Status s : statuses) {
            if (s == status) {
                return true;
            }
        }
        return false;
    }
}
