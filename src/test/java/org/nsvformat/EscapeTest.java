package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

public class EscapeTest {

    @Test
    public void testEscapeUnescapeInvertibility() {
        List<String> testCases = List.of(
            "",
            "hello",
            "hello\nworld",
            "backslash\\here",
            "both\nand\\here",
            "multiple\n\n\nlines",
            "multiple\\\\\\backslashes"
        );

        for (String s : testCases) {
            String escaped = Nsv.escape(s);
            String recovered = Nsv.unescape(escaped);
            assertEquals(s, recovered, "Failed for string: " + s);
        }
    }

    @Test
    public void testEscapeEmptyString() {
        assertEquals("\\", Nsv.escape(""));
    }

    @Test
    public void testUnescapeEmptyCellToken() {
        assertEquals("", Nsv.unescape("\\"));
    }

    @Test
    public void testEscapeNewline() {
        assertEquals("hello\\nworld", Nsv.escape("hello\nworld"));
    }

    @Test
    public void testEscapeBackslash() {
        assertEquals("hello\\\\world", Nsv.escape("hello\\world"));
    }

    @Test
    public void testEscapeBoth() {
        assertEquals("hello\\n\\\\world", Nsv.escape("hello\n\\world"));
    }

    @Test
    public void testUnescapeNewline() {
        assertEquals("hello\nworld", Nsv.unescape("hello\\nworld"));
    }

    @Test
    public void testUnescapeBackslash() {
        assertEquals("hello\\world", Nsv.unescape("hello\\\\world"));
    }

    @Test
    public void testUnescapeBoth() {
        assertEquals("hello\n\\world", Nsv.unescape("hello\\n\\\\world"));
    }

    @Test
    public void testUnescapeUnrecognizedSequencePassesThrough() {
        assertEquals("hello\\xworld", Nsv.unescape("hello\\xworld"));
    }

    @Test
    public void testEscapeSeqseqUsingMap() {
        List<List<String>> seqseq = List.of(
            List.of("a", "b\n"),
            List.of("c\\d", "")
        );
        List<List<String>> expected = List.of(
            List.of("a", "b\\n"),
            List.of("c\\\\d", "\\")
        );

        List<List<String>> result = seqseq.stream()
            .map(row -> row.stream().map(Nsv::escape).collect(Collectors.toList()))
            .collect(Collectors.toList());
        assertEquals(expected, result);

        List<List<String>> recovered = result.stream()
            .map(row -> row.stream().map(Nsv::unescape).collect(Collectors.toList()))
            .collect(Collectors.toList());
        assertEquals(seqseq, recovered);
    }
}
