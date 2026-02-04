package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testEnumValues() {
        assertEquals(4, Status.values().length);
        assertEquals(Status.PROPOSED, Status.values()[0]);
        assertEquals(Status.NEGOTIATED, Status.values()[1]);
        assertEquals(Status.IMPLEMENTED, Status.values()[2]);
        assertEquals(Status.DONE, Status.values()[3]);
    }

    @Test
    void testEnumValueOf() {
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));
        assertEquals(Status.NEGOTIATED, Status.valueOf("NEGOTIATED"));
        assertEquals(Status.IMPLEMENTED, Status.valueOf("IMPLEMENTED"));
        assertEquals(Status.DONE, Status.valueOf("DONE"));
    }

    @Test
    void testAllConstant() {
        assertEquals(4, Status.ALL.length);
        assertEquals(Status.PROPOSED, Status.ALL[0]);
        assertEquals(Status.NEGOTIATED, Status.ALL[1]);
        assertEquals(Status.IMPLEMENTED, Status.ALL[2]);
        assertEquals(Status.DONE, Status.ALL[3]);
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, Status.PROPOSED.ordinal());
        assertEquals(1, Status.NEGOTIATED.ordinal());
        assertEquals(2, Status.IMPLEMENTED.ordinal());
        assertEquals(3, Status.DONE.ordinal());
    }

    @Test
    void testEnumToString() {
        assertEquals("PROPOSED", Status.PROPOSED.toString());
        assertEquals("NEGOTIATED", Status.NEGOTIATED.toString());
        assertEquals("IMPLEMENTED", Status.IMPLEMENTED.toString());
        assertEquals("DONE", Status.DONE.toString());
    }

    @Test
    void testEnumEquality() {
        assertSame(Status.PROPOSED, Status.valueOf("PROPOSED"));
        assertSame(Status.NEGOTIATED, Status.valueOf("NEGOTIATED"));
        assertSame(Status.IMPLEMENTED, Status.valueOf("IMPLEMENTED"));
        assertSame(Status.DONE, Status.valueOf("DONE"));
    }
}