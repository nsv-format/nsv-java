package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReaderTest {

    @Test
    public void testReaderBasic() {
        String input = "a\nb\nc\n\nd\ne\nf\n\n";
        var reader = new Reader(new StringReader(input));

        List<List<String>> rows = new ArrayList<>();
        while (reader.hasNext()) {
            rows.add(reader.next());
        }

        List<List<String>> expected = List.of(
            List.of("a", "b", "c"),
            List.of("d", "e", "f")
        );
        assertEquals(expected, rows);
    }

    @Test
    public void testReaderIncremental() {
        String input = "a\nb\nc\n\nd\ne\nf\n\n";
        var reader = new Reader(new StringReader(input));

        assertTrue(reader.hasNext());
        List<String> first = reader.next();
        assertEquals(List.of("a", "b", "c"), first);

        assertTrue(reader.hasNext());
        List<String> second = reader.next();
        assertEquals(List.of("d", "e", "f"), second);

        assertFalse(reader.hasNext());
    }

    @Test
    public void testReaderEmpty() {
        var reader = new Reader(new StringReader(""));
        assertFalse(reader.hasNext());
    }

    @Test
    public void testReaderSingleEmptyRow() {
        var reader = new Reader(new StringReader("\n"));
        assertTrue(reader.hasNext());
        assertEquals(List.of(), reader.next());
        assertFalse(reader.hasNext());
    }

    @Test
    public void testReaderEmptyFields() {
        String input = "r1c1\n\\\nr1c3\n\nr2c1\n\\\nr2c3\n\n";
        var reader = new Reader(new StringReader(input));

        List<List<String>> rows = new ArrayList<>();
        while (reader.hasNext()) {
            rows.add(reader.next());
        }

        List<List<String>> expected = List.of(
            List.of("r1c1", "", "r1c3"),
            List.of("r2c1", "", "r2c3")
        );
        assertEquals(expected, rows);
    }

    @Test
    public void testReaderMultilineEncoded() {
        String input = "line1\\nline2\nr1c2\nr1c3\n\nanotherline1\\nline2\\nline3\nr2c2\n\n";
        var reader = new Reader(new StringReader(input));

        List<List<String>> rows = new ArrayList<>();
        while (reader.hasNext()) {
            rows.add(reader.next());
        }

        List<List<String>> expected = List.of(
            List.of("line1\nline2", "r1c2", "r1c3"),
            List.of("anotherline1\nline2\nline3", "r2c2")
        );
        assertEquals(expected, rows);
    }

    @Test
    public void testReaderParityWithDecode() {
        List<String> testCases = List.of(
            "",
            "\n",
            "\n\n",
            "a\nb\nc\n\nd\ne\nf\n\n",
            "r1c1\n\\\nr1c3\n\nr2c1\n\\\nr2c3\n\n",
            "\\\n\n"
        );

        for (String input : testCases) {
            var reader = new Reader(new StringReader(input));
            List<List<String>> fromReader = new ArrayList<>();
            while (reader.hasNext()) {
                fromReader.add(reader.next());
            }

            List<List<String>> fromDecode = Nsv.decode(input);
            assertEquals(fromDecode, fromReader, "Parity failed for: " + input);
        }
    }
}
