package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DecodeTest {

    @Test
    public void testDecodeEmpty() {
        assertEquals(List.of(), Nsv.decode(""));
    }

    @Test
    public void testDecodeSingleEmptyRow() {
        assertEquals(List.of(List.of()), Nsv.decode("\n"));
    }

    @Test
    public void testDecodeTwoEmptyRows() {
        assertEquals(List.of(List.of(), List.of()), Nsv.decode("\n\n"));
    }

    @Test
    public void testDecodeBasic() {
        String input = "a\nb\nc\n\nd\ne\nf\n\n";
        List<List<String>> expected = List.of(
            List.of("a", "b", "c"),
            List.of("d", "e", "f")
        );
        assertEquals(expected, Nsv.decode(input));
    }

    @Test
    public void testDecodeEmptyFields() {
        String input = "r1c1\n\\\nr1c3\n\nr2c1\n\\\nr2c3\n\n";
        List<List<String>> expected = List.of(
            List.of("r1c1", "", "r1c3"),
            List.of("r2c1", "", "r2c3")
        );
        assertEquals(expected, Nsv.decode(input));
    }

    @Test
    public void testDecodeEmptySequenceMiddle() {
        String input = "r1c1\nr1c2\n\n\nr3c1\nr3c2\n\n";
        List<List<String>> expected = List.of(
            List.of("r1c1", "r1c2"),
            List.of(),
            List.of("r3c1", "r3c2")
        );
        assertEquals(expected, Nsv.decode(input));
    }

    @Test
    public void testDecodeEmptySequenceStart() {
        String input = "\nr2c1\nr2c2\n\nr3c1\nr3c2\n\n";
        List<List<String>> expected = List.of(
            List.of(),
            List.of("r2c1", "r2c2"),
            List.of("r3c1", "r3c2")
        );
        assertEquals(expected, Nsv.decode(input));
    }

    @Test
    public void testDecodeEmptySequenceEnd() {
        String input = "r1c1\nr1c2\n\nr2c1\nr2c2\n\n\n";
        List<List<String>> expected = List.of(
            List.of("r1c1", "r1c2"),
            List.of("r2c1", "r2c2"),
            List.of()
        );
        assertEquals(expected, Nsv.decode(input));
    }

    @Test
    public void testDecodeMultipleEmptySequences() {
        String input = "\nr2c1\nr2c2\n\n\n\nr5c1\nr5c2\nr5c3\n\n\n";
        List<List<String>> expected = List.of(
            List.of(),
            List.of("r2c1", "r2c2"),
            List.of(),
            List.of(),
            List.of("r5c1", "r5c2", "r5c3"),
            List.of()
        );
        assertEquals(expected, Nsv.decode(input));
    }

    @Test
    public void testDecodeMultilineEncoded() {
        String input = "line1\\nline2\nr1c2\nr1c3\n\nanotherline1\\nline2\\nline3\nr2c2\n\n";
        List<List<String>> expected = List.of(
            List.of("line1\nline2", "r1c2", "r1c3"),
            List.of("anotherline1\nline2\nline3", "r2c2")
        );
        assertEquals(expected, Nsv.decode(input));
    }

    @Test
    public void testDecodeEscapeEdgeCases() {
        String input = "\\\\n\n\\\\\\n\n\\\\\\\\n\n\n\\\\\\\\\n\\n\\n\n\n";
        List<List<String>> expected = List.of(
            List.of("\\n", "\\\n", "\\\\n"),
            List.of("\\\\", "\n\n")
        );
        assertEquals(expected, Nsv.decode(input));
    }

    @Test
    public void testDecodeOneOne() {
        String input = "\\\n\n";
        List<List<String>> expected = List.of(List.of(""));
        assertEquals(expected, Nsv.decode(input));
    }
}
