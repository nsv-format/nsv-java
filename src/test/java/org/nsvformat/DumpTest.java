package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.io.StringWriter;
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
            NsvData data = entry.getValue();
            
            String dumpsResult = Nsv.write(data.rows(), data.metadata());
            
            StringWriter stringWriter = new StringWriter();
            Nsv.write(data.rows(), stringWriter, data.metadata());
            String dumpResult = stringWriter.toString();
            
            assertEquals(dumpsResult, dumpResult, "Parity check failed for sample: " + name);
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