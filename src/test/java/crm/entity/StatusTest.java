package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testStatusValues_Count() {
        assertEquals(4, Status.values().length);
    }

    @Test
    void testStatusValue_Proposed() {
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));
    }

    @Test
    void testStatusValue_Negotiated() {
        assertEquals(Status.NEGOTIATED, Status.valueOf("NEGOTIATED"));
    }

    @Test
    void testStatusValue_Implemented() {
        assertEquals(Status.IMPLEMENTED, Status.valueOf("IMPLEMENTED"));
    }

    @Test
    void testStatusValue_Done() {
        assertEquals(Status.DONE, Status.valueOf("DONE"));
    }

    @Test
    void testStatusAll_ContainsAllValues() {
        assertEquals(4, Status.ALL.length);
        assertArrayEquals(new Status[]{Status.PROPOSED, Status.NEGOTIATED, Status.IMPLEMENTED, Status.DONE}, Status.ALL);
    }

    @Test
    void testStatusAll_ContainsProposed() {
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
    void testStatusAll_ContainsNegotiated() {
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
    void testStatusAll_ContainsImplemented() {
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
    void testStatusAll_ContainsDone() {
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
    void testStatusOrdinal_Proposed() {
        assertEquals(0, Status.PROPOSED.ordinal());
    }

    @Test
    void testStatusOrdinal_Negotiated() {
        assertEquals(1, Status.NEGOTIATED.ordinal());
    }

    @Test
    void testStatusOrdinal_Implemented() {
        assertEquals(2, Status.IMPLEMENTED.ordinal());
    }

    @Test
    void testStatusOrdinal_Done() {
        assertEquals(3, Status.DONE.ordinal());
    }

    @Test
    void testStatusName_Proposed() {
        assertEquals("PROPOSED", Status.PROPOSED.name());
    }

    @Test
    void testStatusName_Done() {
        assertEquals("DONE", Status.DONE.name());
    }

    @Test
    void testInvalidStatusValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> Status.valueOf("INVALID"));
    }

    @Test
    void testStatusEquality() {
        Status s1 = Status.PROPOSED;
        Status s2 = Status.PROPOSED;
        assertEquals(s1, s2);
    }

    @Test
    void testStatusInequality() {
        assertNotEquals(Status.PROPOSED, Status.DONE);
    }
}
