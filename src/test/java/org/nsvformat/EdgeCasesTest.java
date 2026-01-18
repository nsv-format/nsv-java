package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EdgeCasesTest {

    @Test
    public void testLongStrings() {
        StringBuilder sb = new StringBuilder();
        for (int x = 11; x < 50000; x++) {
            sb.append((char) x);
        }
        String longString = sb.toString();
        
        List<List<String>> data = List.of(
            List.of("normal", longString),
            List.of(longString, "normal")
        );
        
        List<List<String>> result = TestUtils.dumpThenLoad(data);
        assertEquals(data, result);
    }

    @Test
    public void testSpecialCharacters() {
        List<List<String>> expected = TestUtils.SAMPLES_DATA.get("special_chars");
        List<List<String>> actual = TestUtils.loadSample("special_chars");
        assertEquals(expected, actual);
    }

    @Test
    public void testTrailingBackslashHandling() {
        // Dangling backslash should be stripped per spec
        String input = "yo\nshouln'ta\nbe\ndoing\nthis\\\n\n\\\nor\n\\\nshould\n\\\nya\n\n";
        List<List<String>> expected = List.of(
            List.of("yo", "shouln'ta", "be", "doing", "this"),
            List.of("", "or", "", "should", "", "ya")
        );
        List<List<String>> decoded = Nsv.decode(input);
        assertEquals(expected, decoded);
    }

    @Test
    public void testUnrecognizedEscapeSequences() {
        // Per spec, unrecognized sequences pass through with literal backslash
        String input = "\\x\\y\\z\n\n";
        List<List<String>> expected = List.of(List.of("\\x\\y\\z"));
        List<List<String>> decoded = Nsv.decode(input);
        assertEquals(expected, decoded);
    }

    @Test
    public void testEmptyStringVsEmptyCellToken() {
        // Empty cell token becomes empty string
        assertEquals("", Nsv.unescape("\\"));
        // Empty string encodes to empty cell token
        assertEquals("\\", Nsv.escape(""));
    }

    @Test
    public void testMultipleConsecutiveEmptyRows() {
        List<List<String>> data = List.of(
            List.of(),
            List.of("r2c1", "r2c2"),
            List.of(),
            List.of(),
            List.of("r5c1", "r5c2", "r5c3"),
            List.of()
        );

        String encoded = Nsv.encode(data);
        List<List<String>> decoded = Nsv.decode(encoded);
        assertEquals(data, decoded);
    }

    @Test
    public void testEscapeExplosion() {
        // Repeated encoding should increase backslashes
        String original = "\\";
        String once = Nsv.escape(original);
        assertEquals("\\\\", once);

        String twice = Nsv.escape(once);
        assertEquals("\\\\\\\\", twice);

        // And unescape should reverse it
        assertEquals(once, Nsv.unescape(twice));
        assertEquals(original, Nsv.unescape(once));
    }

    @Test
    public void testNewlineExplosion() {
        String original = "\n";
        String once = Nsv.escape(original);
        assertEquals("\\n", once);

        String twice = Nsv.escape(once);
        assertEquals("\\\\n", twice);

        assertEquals(once, Nsv.unescape(twice));
        assertEquals(original, Nsv.unescape(once));
    }
}