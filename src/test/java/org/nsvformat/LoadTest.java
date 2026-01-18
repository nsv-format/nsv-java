package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LoadTest {

    @Test
    public void testLoad() {
        for (var entry : TestUtils.SAMPLES_DATA.entrySet()) {
            String name = entry.getKey();
            List<List<String>> expected = entry.getValue();
            List<List<String>> actual = TestUtils.loadSample(name);
            assertEquals(expected, actual, "Failed for sample: " + name);
        }
    }

    @Test
    public void testLoads() {
        for (var entry : TestUtils.SAMPLES_DATA.entrySet()) {
            String name = entry.getKey();
            List<List<String>> expected = entry.getValue();
            List<List<String>> actual = TestUtils.loadsSample(name);
            assertEquals(expected, actual, "Failed for sample: " + name);
        }
    }

    @Test
    public void testParity() {
        for (String name : TestUtils.SAMPLES_DATA.keySet()) {
            try (var inputStream = LoadTest.class.getResourceAsStream("/samples/" + name + ".nsv")) {
                assertNotNull(inputStream, "Sample file not found: " + name + ".nsv");
                String content = new String(inputStream.readAllBytes());
                
                List<List<String>> fromString = Nsv.decode(content);

                var reader = new Reader(new StringReader(content));
                List<List<String>> fromReader = new java.util.ArrayList<>();
                while (reader.hasNext()) {
                    fromReader.add(reader.next());
                }
                
                assertEquals(fromString, fromReader, "Parity check failed for sample: " + name);
            } catch (Exception e) {
                fail("Failed to read sample file: " + name, e);
            }
        }
    }
}