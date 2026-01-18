package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WriterTest {

    @Test
    public void testWriterBasic() throws IOException {
        List<List<String>> data = List.of(
            List.of("a", "b", "c"),
            List.of("d", "e", "f")
        );
        var output = new StringWriter();
        var writer = new Writer(output);

        writer.writeRows(data);

        String expected = "a\nb\nc\n\nd\ne\nf\n\n";
        assertEquals(expected, output.toString());
    }

    @Test
    public void testWriterIncremental() throws IOException {
        var output = new StringWriter();
        var writer = new Writer(output);

        writer.writeRow(List.of("a", "b", "c"));
        writer.writeRow(List.of("d", "e", "f"));

        String expected = "a\nb\nc\n\nd\ne\nf\n\n";
        assertEquals(expected, output.toString());
    }

    @Test
    public void testWriterEmpty() throws IOException {
        var output = new StringWriter();
        var writer = new Writer(output);
        writer.writeRows(List.of());
        assertEquals("", output.toString());
    }

    @Test
    public void testWriterSingleEmptyRow() throws IOException {
        var output = new StringWriter();
        var writer = new Writer(output);
        writer.writeRow(List.of());
        assertEquals("\n", output.toString());
    }

    @Test
    public void testWriterEmptyFields() throws IOException {
        List<List<String>> data = List.of(
            List.of("r1c1", "", "r1c3"),
            List.of("r2c1", "", "r2c3")
        );
        var output = new StringWriter();
        var writer = new Writer(output);

        writer.writeRows(data);

        String expected = "r1c1\n\\\nr1c3\n\nr2c1\n\\\nr2c3\n\n";
        assertEquals(expected, output.toString());
    }

    @Test
    public void testWriterMultilineEncoded() throws IOException {
        List<List<String>> data = List.of(
            List.of("line1\nline2", "r1c2", "r1c3"),
            List.of("anotherline1\nline2\nline3", "r2c2")
        );
        var output = new StringWriter();
        var writer = new Writer(output);

        writer.writeRows(data);

        String expected = "line1\\nline2\nr1c2\nr1c3\n\nanotherline1\\nline2\\nline3\nr2c2\n\n";
        assertEquals(expected, output.toString());
    }

    @Test
    public void testWriterParityWithEncode() throws IOException {
        List<List<List<String>>> testCases = List.of(
            List.of(),
            List.of(List.of()),
            List.of(List.of(), List.of()),
            List.of(List.of("a", "b", "c"), List.of("d", "e", "f")),
            List.of(List.of("r1c1", "", "r1c3"), List.of("r2c1", "", "r2c3")),
            List.of(List.of(""))
        );

        for (List<List<String>> data : testCases) {
            var output = new StringWriter();
            var writer = new Writer(output);
            writer.writeRows(data);

            String fromWriter = output.toString();
            String fromEncode = Nsv.encode(data);
            assertEquals(fromEncode, fromWriter, "Parity failed for: " + data);
        }
    }

    @Test
    public void testWriterReaderRoundtrip() throws IOException {
        List<List<String>> data = List.of(
            List.of("a", "b", "c"),
            List.of("d", "e", "f")
        );

        var output = new StringWriter();
        var writer = new Writer(output);
        writer.writeRows(data);

        var reader = new Reader(new StringReader(output.toString()));
        List<List<String>> recovered = new ArrayList<>();
        while (reader.hasNext()) {
            recovered.add(reader.next());
        }

        assertEquals(data, recovered);
    }
}
