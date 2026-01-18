package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.io.StringWriter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DumpTest {

    @Test
    public void testDump() {
        for (String name : TestUtils.SAMPLES_DATA.keySet()) {
            String actual = TestUtils.dumpSample(name);
            String expected = loadSampleFileContent(name);
            assertEquals(expected, actual, "Failed for sample: " + name);
        }
    }

    @Test
    public void testDumps() {
        for (String name : TestUtils.SAMPLES_DATA.keySet()) {
            String actual = TestUtils.dumpsSample(name);
            String expected = loadSampleFileContent(name);
            assertEquals(expected, actual, "Failed for sample: " + name);
        }
    }

    @Test
    public void testParity() {
        for (var entry : TestUtils.SAMPLES_DATA.entrySet()) {
            String name = entry.getKey();
            List<List<String>> data = entry.getValue();
            
            String encodeResult = Nsv.encode(data);

            StringWriter stringWriter = new StringWriter();
            try {
                var writer = new Writer(stringWriter);
                writer.writeRows(data);
            } catch (Exception e) {
                fail("Failed to write data for sample: " + name, e);
            }
            String writerResult = stringWriter.toString();
            
            assertEquals(encodeResult, writerResult, "Parity check failed for sample: " + name);
        }
    }

    private String loadSampleFileContent(String name) {
        try (var inputStream = DumpTest.class.getResourceAsStream("/samples/" + name + ".nsv")) {
            assertNotNull(inputStream, "Sample file not found: " + name + ".nsv");
            return new String(inputStream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read sample file: " + name, e);
        }
    }
}