package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EncodeTest {

    @Test
    public void testEncodeEmpty() {
        assertEquals("", Nsv.encode(List.of()));
    }

    @Test
    public void testEncodeSingleEmptyRow() {
        assertEquals("\n", Nsv.encode(List.of(List.of())));
    }

    @Test
    public void testEncodeTwoEmptyRows() {
        assertEquals("\n\n", Nsv.encode(List.of(List.of(), List.of())));
    }

    @Test
    public void testEncodeBasic() {
        var input = List.of(
            List.of("a", "b", "c"),
            List.of("d", "e", "f")
        );
        String expected = "a\nb\nc\n\nd\ne\nf\n\n";
        assertEquals(expected, Nsv.encode(input));
    }

    @Test
    public void testEncodeEmptyFields() {
        var input = List.of(
            List.of("r1c1", "", "r1c3"),
            List.of("r2c1", "", "r2c3")
        );
        String expected = "r1c1\n\\\nr1c3\n\nr2c1\n\\\nr2c3\n\n";
        assertEquals(expected, Nsv.encode(input));
    }

    @Test
    public void testEncodeDecodeInvertibility() {
        List<List<List<String>>> testCases = List.of(
            List.of(),
            List.of(List.of()),
            List.of(List.of(), List.of()),
            List.of(List.of("a", "b", "c"), List.of("d", "e", "f")),
            List.of(List.of("r1c1", "", "r1c3"), List.of("r2c1", "", "r2c3")),
            List.of(List.of("r1c1", "r1c2"), List.of(), List.of("r3c1", "r3c2")),
            List.of(List.of(), List.of("r2c1", "r2c2"), List.of("r3c1", "r3c2")),
            List.of(List.of("r1c1", "r1c2"), List.of("r2c1", "r2c2"), List.of()),
            List.of(
                List.of(),
                List.of("r2c1", "r2c2"),
                List.of(),
                List.of(),
                List.of("r5c1", "r5c2", "r5c3"),
                List.of()
            ),
            List.of(
                List.of("line1\nline2", "r1c2", "r1c3"),
                List.of("anotherline1\nline2\nline3", "r2c2")
            ),
            List.of(
                List.of("\\n", "\\\n", "\\\\n"),
                List.of("\\\\", "\n\n")
            ),
            List.of(List.of(""))
        );

        for (List<List<String>> data : testCases) {
            String encoded = Nsv.encode(data);
            List<List<String>> decoded = Nsv.decode(encoded);
            assertEquals(data, decoded, "Failed for: " + data);
        }
    }

    @Test
    public void testDecodeEncodeInvertibilityForValidNsvStrings() {
        List<String> testCases = List.of(
            "",
            "\n",
            "\n\n",
            "a\nb\nc\n\nd\ne\nf\n\n",
            "r1c1\n\\\nr1c3\n\nr2c1\n\\\nr2c3\n\n",
            "r1c1\nr1c2\n\n\nr3c1\nr3c2\n\n",
            "\nr2c1\nr2c2\n\nr3c1\nr3c2\n\n",
            "r1c1\nr1c2\n\nr2c1\nr2c2\n\n\n",
            "\\\n\n"
        );

        for (String s : testCases) {
            List<List<String>> decoded = Nsv.decode(s);
            String encoded = Nsv.encode(decoded);
            assertEquals(s, encoded, "Failed for: " + s);
        }
    }
}
