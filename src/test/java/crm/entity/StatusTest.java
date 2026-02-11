package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testStatusEnumValues() {
        Status[] statuses = Status.values();
        assertEquals(4, statuses.length);
    }

    @Test
    void testProposedStatus() {
        Status status = Status.PROPOSED;
        assertNotNull(status);
        assertEquals("PROPOSED", status.name());
    }

    @Test
    void testNegotiatedStatus() {
        Status status = Status.NEGOTIATED;
        assertNotNull(status);
        assertEquals("NEGOTIATED", status.name());
    }

    @Test
    void testImplementedStatus() {
        Status status = Status.IMPLEMENTED;
        assertNotNull(status);
        assertEquals("IMPLEMENTED", status.name());
    }

    @Test
    void testDoneStatus() {
        Status status = Status.DONE;
        assertNotNull(status);
        assertEquals("DONE", status.name());
    }

    @Test
    void testStatusValueOf() {
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));
        assertEquals(Status.NEGOTIATED, Status.valueOf("NEGOTIATED"));
        assertEquals(Status.IMPLEMENTED, Status.valueOf("IMPLEMENTED"));
        assertEquals(Status.DONE, Status.valueOf("DONE"));
    }

    @Test
    void testStatusValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Status.valueOf("INVALID"));
    }

    @Test
    void testStatusALLArray() {
        Status[] allStatuses = Status.ALL;

        assertNotNull(allStatuses);
        assertEquals(4, allStatuses.length);
        assertEquals(Status.PROPOSED, allStatuses[0]);
        assertEquals(Status.NEGOTIATED, allStatuses[1]);
        assertEquals(Status.IMPLEMENTED, allStatuses[2]);
        assertEquals(Status.DONE, allStatuses[3]);
    }

    @Test
    void testStatusOrdinal() {
        assertEquals(0, Status.PROPOSED.ordinal());
        assertEquals(1, Status.NEGOTIATED.ordinal());
        assertEquals(2, Status.IMPLEMENTED.ordinal());
        assertEquals(3, Status.DONE.ordinal());
    }

    @Test
    void testStatusComparison() {
        assertTrue(Status.PROPOSED.ordinal() < Status.NEGOTIATED.ordinal());
        assertTrue(Status.NEGOTIATED.ordinal() < Status.IMPLEMENTED.ordinal());
        assertTrue(Status.IMPLEMENTED.ordinal() < Status.DONE.ordinal());
    }

    @Test
    void testStatusEquality() {
        Status status1 = Status.PROPOSED;
        Status status2 = Status.PROPOSED;

        assertEquals(status1, status2);
        assertSame(status1, status2);
    }

    @Test
    void testStatusInequality() {
        Status status1 = Status.PROPOSED;
        Status status2 = Status.NEGOTIATED;

        assertNotEquals(status1, status2);
    }

    @Test
    void testStatusToString() {
        assertEquals("PROPOSED", Status.PROPOSED.toString());
        assertEquals("NEGOTIATED", Status.NEGOTIATED.toString());
        assertEquals("IMPLEMENTED", Status.IMPLEMENTED.toString());
        assertEquals("DONE", Status.DONE.toString());
    }

    @Test
    void testStatusName() {
        assertEquals("PROPOSED", Status.PROPOSED.name());
        assertEquals("NEGOTIATED", Status.NEGOTIATED.name());
        assertEquals("IMPLEMENTED", Status.IMPLEMENTED.name());
        assertEquals("DONE", Status.DONE.name());
    }

    @Test
    void testSwitchStatement() {
        String result = "";
        Status status = Status.PROPOSED;

        switch (status) {
            case PROPOSED:
                result = "Contract is proposed";
                break;
            case NEGOTIATED:
                result = "Contract is negotiated";
                break;
            case IMPLEMENTED:
                result = "Contract is implemented";
                break;
            case DONE:
                result = "Contract is done";
                break;
        }

        assertEquals("Contract is proposed", result);
    }

    @Test
    void testAllStatusesInLoop() {
        for (Status status : Status.ALL) {
            assertNotNull(status);
            assertTrue(status.name().length() > 0);
        }
    }

    @Test
    void testStatusValuesMatchALL() {
        Status[] values = Status.values();
        Status[] all = Status.ALL;

        assertEquals(values.length, all.length);

        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], all[i]);
        }
    }

    @Test
    void testEnumHashCode() {
        int hashCode1 = Status.PROPOSED.hashCode();
        int hashCode2 = Status.PROPOSED.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testEnumInSet() {
        java.util.Set<Status> statusSet = java.util.EnumSet.allOf(Status.class);

        assertEquals(4, statusSet.size());
        assertTrue(statusSet.contains(Status.PROPOSED));
        assertTrue(statusSet.contains(Status.NEGOTIATED));
        assertTrue(statusSet.contains(Status.IMPLEMENTED));
        assertTrue(statusSet.contains(Status.DONE));
    }
}
