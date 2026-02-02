package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testStatusEnumValues() {
        // Assert that enum values are correctly defined
        assertEquals(4, Status.values().length);
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));
        assertEquals(Status.NEGOTIATED, Status.valueOf("NEGOTIATED"));
        assertEquals(Status.IMPLEMENTED, Status.valueOf("IMPLEMENTED"));
        assertEquals(Status.DONE, Status.valueOf("DONE"));
    }

    @Test
    void testStatusEnumOrder() {
        // Assert that enum values are in the expected order
        Status[] values = Status.values();
        assertEquals(Status.PROPOSED, values[0]);
        assertEquals(Status.NEGOTIATED, values[1]);
        assertEquals(Status.IMPLEMENTED, values[2]);
        assertEquals(Status.DONE, values[3]);
    }

    @Test
    void testAllConstant() {
        // Assert that ALL constant contains all enum values in the correct order
        assertEquals(4, Status.ALL.length);
        assertEquals(Status.PROPOSED, Status.ALL[0]);
        assertEquals(Status.NEGOTIATED, Status.ALL[1]);
        assertEquals(Status.IMPLEMENTED, Status.ALL[2]);
        assertEquals(Status.DONE, Status.ALL[3]);

        // Assert that ALL contains the same values as Status.values()
        assertArrayEquals(Status.values(), Status.ALL);
    }
}