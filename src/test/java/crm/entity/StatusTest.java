package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testStatusProposed() {
        Status status = Status.PROPOSED;
        assertEquals("PROPOSED", status.name());
    }

    @Test
    void testStatusNegotiated() {
        Status status = Status.NEGOTIATED;
        assertEquals("NEGOTIATED", status.name());
    }

    @Test
    void testStatusImplemented() {
        Status status = Status.IMPLEMENTED;
        assertEquals("IMPLEMENTED", status.name());
    }

    @Test
    void testStatusDone() {
        Status status = Status.DONE;
        assertEquals("DONE", status.name());
    }

    @Test
    void testStatusAllArray() {
        Status[] all = Status.ALL;
        assertNotNull(all);
        assertEquals(4, all.length);
    }

    @Test
    void testStatusAllContainsProposed() {
        boolean found = false;
        for (Status s : Status.ALL) {
            if (s == Status.PROPOSED) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testStatusAllContainsNegotiated() {
        boolean found = false;
        for (Status s : Status.ALL) {
            if (s == Status.NEGOTIATED) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testStatusAllContainsImplemented() {
        boolean found = false;
        for (Status s : Status.ALL) {
            if (s == Status.IMPLEMENTED) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testStatusAllContainsDone() {
        boolean found = false;
        for (Status s : Status.ALL) {
            if (s == Status.DONE) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testStatusValues() {
        Status[] values = Status.values();
        assertEquals(4, values.length);
    }

    @Test
    void testStatusValueOf_Proposed() {
        Status status = Status.valueOf("PROPOSED");
        assertEquals(Status.PROPOSED, status);
    }

    @Test
    void testStatusValueOf_Negotiated() {
        Status status = Status.valueOf("NEGOTIATED");
        assertEquals(Status.NEGOTIATED, status);
    }

    @Test
    void testStatusValueOf_Implemented() {
        Status status = Status.valueOf("IMPLEMENTED");
        assertEquals(Status.IMPLEMENTED, status);
    }

    @Test
    void testStatusValueOf_Done() {
        Status status = Status.valueOf("DONE");
        assertEquals(Status.DONE, status);
    }

    @Test
    void testStatusOrdinal() {
        assertEquals(0, Status.PROPOSED.ordinal());
        assertEquals(1, Status.NEGOTIATED.ordinal());
        assertEquals(2, Status.IMPLEMENTED.ordinal());
        assertEquals(3, Status.DONE.ordinal());
    }

    @Test
    void testStatusInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> Status.valueOf("INVALID"));
    }
}
