package org.nsvformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class TestUtils {
    
    public static final Map<String, NsvData> SAMPLES_DATA = Map.ofEntries(
        entry("empty", new NsvData(List.of("v:1.0"), List.of())),
        entry("empty_one", new NsvData(List.of("v:1.0"), List.of(List.of()))),
        entry("empty_two", new NsvData(List.of("v:1.0"), List.of(List.of(), List.of()))),
        entry("empty_three", new NsvData(List.of("v:1.0"), List.of(List.of(), List.of(), List.of()))),
        entry("basic", new NsvData(List.of("v:1.0"), List.of(
            List.of("r1c1", "r1c2", "r1c3"),
            List.of("r2c1", "r2c2", "r2c3")
        ))),
        entry("comments", new NsvData(
            List.of("v:1.0", "# This is a comment", "// Another comment", "-- And another"),
            List.of(List.of("r1c1", "r1c2"), List.of("r2c1", "r2c2"))
        )),
        entry("empty_fields", new NsvData(List.of("v:1.0"), List.of(
            List.of("r1c1", "", "r1c3"),
            List.of("r2c1", "", "r2c3")
        ))),
        entry("empty_sequence", new NsvData(List.of("v:1.0"), List.of(
            List.of("r1c1", "r1c2"), 
            List.of(), 
            List.of("r3c1", "r3c2")
        ))),
        entry("empty_sequence_end", new NsvData(List.of("v:1.0"), List.of(
            List.of("r1c1", "r1c2"), 
            List.of("r2c1", "r2c2"), 
            List.of()
        ))),
        entry("empty_sequence_start", new NsvData(List.of("v:1.0"), List.of(
            List.of(), 
            List.of("r2c1", "r2c2"), 
            List.of("r3c1", "r3c2")
        ))),
        entry("special_chars", new NsvData(List.of("v:1.0"), List.of(
            List.of("field with spaces", "field,with,commas", "field\twith\ttabs"),
            List.of("field\"with\"quotes", "field'with'quotes", "field\\with\\backslashes"),
            List.of("field\nwith\nnewlines", "field, just field")
        ))),
        entry("multiple_empty_sequences", new NsvData(List.of("v:1.0"), List.of(
            List.of(),
            List.of("r2c1", "r2c2"),
            List.of(),
            List.of(),
            List.of("r5c1", "r5c2", "r5c3"),
            List.of()
        ))),
        entry("multiline_encoded", new NsvData(List.of("v:1.0"), List.of(
            List.of("line1\nline2", "r1c2", "r1c3"),
            List.of("anotherline1\nline2\nline3", "r2c2")
        )))
    );
    
    public static NsvData loadSample(String name) {
        try (InputStream inputStream = TestUtils.class.getResourceAsStream("/samples/" + name + ".nsv")) {
            if (inputStream == null) {
                throw new RuntimeException("Sample file not found: " + name + ".nsv");
            }
            return Nsv.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample: " + name, e);
        }
    }
    
    public static NsvData loadsSample(String name) {
        try (InputStream inputStream = TestUtils.class.getResourceAsStream("/samples/" + name + ".nsv")) {
            if (inputStream == null) {
                throw new RuntimeException("Sample file not found: " + name + ".nsv");
            }
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return Nsv.read(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample: " + name, e);
        }
    }
    
    public static String dumpSample(String name) {
        NsvData sampleData = SAMPLES_DATA.get(name);
        if (sampleData == null) {
            throw new RuntimeException("Sample data not found: " + name);
        }
        return Nsv.write(sampleData.rows(), sampleData.metadata());
    }
    
    public static String dumpsSample(String name) {
        return dumpSample(name); // Same implementation for Java
    }
    
    public static NsvData dumpThenLoad(List<List<String>> data) {
        String dumped = Nsv.write(data);
        return Nsv.read(dumped);
    }
    
    public static String loadThenDump(String content) {
        NsvData loaded = Nsv.read(content);
        return Nsv.write(loaded.rows(), loaded.metadata());
    }
}