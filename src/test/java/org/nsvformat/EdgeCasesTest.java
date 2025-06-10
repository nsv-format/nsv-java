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
        
        NsvData result = TestUtils.dumpThenLoad(data);
        assertEquals(data, result.rows());
    }

    @Test
    public void testSpecialCharacters() {
        NsvData expected = TestUtils.SAMPLES_DATA.get("special_chars");
        NsvData actual = TestUtils.loadSample("special_chars");
        assertEquals(expected, actual);
    }
}