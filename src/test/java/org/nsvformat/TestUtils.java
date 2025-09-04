package org.nsvformat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class TestUtils {
    
    // Match Python test data exactly
    public static final Map<String, List<List<String>>> SAMPLES_DATA = Map.ofEntries(
        entry("empty", List.of()),
        entry("empty_one", List.of(List.of())),
        entry("empty_two", List.of(List.of(), List.of())),
        entry("empty_three", List.of(List.of(), List.of(), List.of())),
        entry("basic", List.of(
            List.of("a", "b", "c"),
            List.of("d", "e", "f")
        )),
        entry("comments", List.of(
            List.of("# This is a comment", "// Another comment", "-- And another"),
            List.of("---"),
            List.of("r1c1", "r1c2"),
            List.of("r2c1", "r2c2")
        )),
        entry("empty_fields", List.of(
            List.of("r1c1", "", "r1c3"),
            List.of("r2c1", "", "r2c3")
        )),
        entry("empty_sequence", List.of(
            List.of("r1c1", "r1c2"), 
            List.of(), 
            List.of("r3c1", "r3c2")
        )),
        entry("empty_sequence_end", List.of(
            List.of("r1c1", "r1c2"), 
            List.of("r2c1", "r2c2"), 
            List.of()
        )),
        entry("empty_sequence_start", List.of(
            List.of(), 
            List.of("r2c1", "r2c2"), 
            List.of("r3c1", "r3c2")
        )),
        entry("special_chars", List.of(
            List.of("field with spaces", "field,with,commas", "field\twith\ttabs"),
            List.of("field\"with\"quotes", "field'with'quotes", "field\\with\\backslashes"),
            List.of("field\nwith\nnewlines", "field, just field")
        )),
        entry("multiple_empty_sequences", List.of(
            List.of(),
            List.of("r2c1", "r2c2"),
            List.of(),
            List.of(),
            List.of("r5c1", "r5c2", "r5c3"),
            List.of()
        )),
        entry("multiline_encoded", List.of(
            List.of("line1\nline2", "r1c2", "r1c3"),
            List.of("anotherline1\nline2\nline3", "r2c2")
        ))
    );
    
    public static List<List<String>> loadSample(String name) {
        try (InputStream inputStream = TestUtils.class.getResourceAsStream("/samples/" + name + ".nsv")) {
            if (inputStream == null) {
                throw new RuntimeException("Sample file not found: " + name + ".nsv");
            }
            return Nsv.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample: " + name, e);
        }
    }
    
    public static List<List<String>> loadsSample(String name) {
        try (InputStream inputStream = TestUtils.class.getResourceAsStream("/samples/" + name + ".nsv")) {
            if (inputStream == null) {
                throw new RuntimeException("Sample file not found: " + name + ".nsv");
            }
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return Nsv.loads(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample: " + name, e);
        }
    }
    
    public static String dumpSample(String name) {
        List<List<String>> sampleData = SAMPLES_DATA.get(name);
        if (sampleData == null) {
            throw new RuntimeException("Sample data not found: " + name);
        }
        return Nsv.dumps(sampleData);
    }
    
    public static String dumpsSample(String name) {
        return dumpSample(name);
    }
    
    public static List<List<String>> dumpThenLoad(List<List<String>> data) {
        String dumped = Nsv.dumps(data);
        return Nsv.loads(dumped);
    }
    
    public static String loadThenDump(String content) {
        List<List<String>> loaded = Nsv.loads(content);
        return Nsv.dumps(loaded);
    }
}