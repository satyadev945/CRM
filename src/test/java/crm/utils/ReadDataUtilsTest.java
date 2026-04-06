package crm.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadDataUtilsTest {

    @Test
    void readDataUtils_shouldBeInstantiable() {
        // Act
        ReadDataUtils readDataUtils = new ReadDataUtils();

        // Assert
        assertNotNull(readDataUtils);
    }

    @Test
    void readDataUtils_shouldHaveReadFileMethod() throws NoSuchMethodException {
        // Assert
        assertNotNull(ReadDataUtils.class.getMethod("ReadFile", String.class, javax.swing.JFrame.class, String.class, String[].class));
    }

    @Test
    void readFile_shouldBeStaticMethod() throws NoSuchMethodException {
        // Assert
        assertTrue(java.lang.reflect.Modifier.isStatic(
                ReadDataUtils.class.getMethod("ReadFile", String.class, javax.swing.JFrame.class, String.class, String[].class).getModifiers()
        ));
    }

    @Test
    void readFile_shouldReturnFile() throws NoSuchMethodException {
        // Assert
        assertEquals(java.io.File.class, 
                ReadDataUtils.class.getMethod("ReadFile", String.class, javax.swing.JFrame.class, String.class, String[].class).getReturnType());
    }
}
