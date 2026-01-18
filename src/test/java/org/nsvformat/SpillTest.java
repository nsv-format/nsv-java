package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SpillTest {

    @Test
    public void testSpillInvertibility() {
        List<List<List<String>>> cases = List.of(
            List.of(),
            List.of(List.of()),
            List.of(List.of(), List.of()),
            List.of(List.of("a")),
            List.of(List.of("a", "b"), List.of("c")),
            List.of(List.of("a"), List.of(), List.of("b"))
        );

        for (List<List<String>> seqseq : cases) {
            List<String> spilled = Util.spill(seqseq, "");
            List<List<String>> recovered = Util.unspill(spilled, "");
            assertEquals(seqseq, recovered);
        }
    }

    @Test
    public void testUnspillInvertibility() {
        List<List<String>> cases = List.of(
            List.of(),
            List.of(""),
            List.of("", ""),
            List.of("a", ""),
            List.of("a", "b", "", "c", ""),
            List.of("a", "", "", "b", "")
        );

        for (List<String> seq : cases) {
            List<List<String>> unspilled = Util.unspill(seq, "");
            List<String> recovered = Util.spill(unspilled, "");
            assertEquals(seq, recovered);
        }
    }

    @Test
    public void testSpillWithDifferentTypes() {
        // spill[String, '']
        var strings2d = List.of(List.of("a", "b"), List.of("c"));
        var result1 = Util.spill(strings2d, "");
        var expected1 = List.of("a", "b", "", "c", "");
        assertEquals(expected1, result1);

        // spill[Integer, -1]
        var ints = List.of(List.of(1, 2), List.of(3));
        var result2 = Util.spill(ints, -1);
        var expected2 = List.of(1, 2, -1, 3, -1);
        assertEquals(expected2, result2);
    }
}
