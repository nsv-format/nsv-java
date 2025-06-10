package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

public class LoadTest {

    @Test
    public void testLoad() {
        for (var entry : TestUtils.SAMPLES_DATA.entrySet()) {
            String name = entry.getKey();
            NsvData expected = entry.getValue();
            NsvData actual = TestUtils.loadSample(name);
            assertEquals(expected, actual, "Failed for sample: " + name);
        }
    }

    @Test
    public void testLoads() {
        for (var entry : TestUtils.SAMPLES_DATA.entrySet()) {
            String name = entry.getKey();
            NsvData expected = entry.getValue();
            NsvData actual = TestUtils.loadsSample(name);
            assertEquals(expected, actual, "Failed for sample: " + name);
        }
    }

    @Test
    public void testParity() {
        for (String name : TestUtils.SAMPLES_DATA.keySet()) {
            try (var inputStream = LoadTest.class.getResourceAsStream("/samples/" + name + ".nsv")) {
                assertNotNull(inputStream, "Sample file not found: " + name + ".nsv");
                String content = new String(inputStream.readAllBytes());
                
                NsvData fromString = Nsv.read(content);
                NsvData fromReader = Nsv.read(new StringReader(content));
                
                assertEquals(fromString, fromReader, "Parity check failed for sample: " + name);
            } catch (Exception e) {
                fail("Failed to read sample file: " + name, e);
            }
        }
    }

    @Test
    public void testMissingSeparator() {
        assertThrows(Exception.class, () -> {
            TestUtils.loadSample("missing_separator");
        }, "Expected exception for missing separator");
    }
}