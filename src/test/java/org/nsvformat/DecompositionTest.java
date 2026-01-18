package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static java.util.stream.Collectors.toList;

public class DecompositionTest {

    @Test
    public void testEncodeDecomposition() {
        List<List<String>> data = List.of(
            List.of("a", "b", "c"),
            List.of("d", "e", "f")
        );

        // Direct encode
        String encoded1 = Nsv.encode(data);

        // Via decomposition: spill[Char, '\n'] ∘ spill[String, ''] ∘ map(map(escape))
        // Step 1: map(map(escape))
        var escaped = data.stream()
            .map(row -> row.stream().map(Nsv::escape).collect(toList()))
            .collect(toList());

        // Step 2: spill[String, '']
        var spilledStrings = Util.spill(escaped, "");

        // Step 3: spill[Char, '\n']
        // Convert strings to List<Character>, spill, then join back
        var spilledChars = Util.spill(
            spilledStrings.stream()
                .map(s -> s.chars().mapToObj(c -> (char)c).collect(toList()))
                .collect(toList()),
            '\n'
        );
        String encoded2 = spilledChars.stream()
            .map(String::valueOf)
            .collect(Collectors.joining());

        assertEquals(encoded1, encoded2);
    }

    @Test
    public void testDecodeDecomposition() {
        String input = "a\nb\nc\n\nd\ne\nf\n\n";

        // Direct decode
        var decoded1 = Nsv.decode(input);

        // Via decomposition: map(map(unescape)) ∘ unspill[String, ''] ∘ unspill[Char, '\n']
        // Step 1: unspill[Char, '\n']
        var chars = input.chars().mapToObj(c -> (char)c).collect(toList());
        var lines = Util.unspill(chars, '\n');

        // Step 2: Convert List<List<Character>> back to List<String>
        var strings = lines.stream()
            .map(charList -> charList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining()))
            .collect(toList());

        // Step 3: unspill[String, '']
        var rows = Util.unspill(strings, "");

        // Step 4: map(map(unescape))
        var decoded2 = rows.stream()
            .map(row -> row.stream().map(Nsv::unescape).collect(toList()))
            .collect(toList());

        assertEquals(decoded1, decoded2);
    }

    @Test
    public void testEncodeDecodeDecompositionRoundtrip() {
        List<List<String>> original = List.of(
            List.of("hello\nworld", "test"),
            List.of("", "backslash\\here"),
            List.of()
        );

        // Encode via decomposition
        var escaped = original.stream()
            .map(row -> row.stream().map(Nsv::escape).collect(toList()))
            .collect(toList());
        var spilledStrings = Util.spill(escaped, "");
        var spilledChars = Util.spill(
            spilledStrings.stream()
                .map(s -> s.chars().mapToObj(c -> (char)c).collect(toList()))
                .collect(toList()),
            '\n'
        );
        String encoded = spilledChars.stream()
            .map(String::valueOf)
            .collect(Collectors.joining());

        // Decode via decomposition
        var chars = encoded.chars().mapToObj(c -> (char)c).collect(toList());
        var lines = Util.unspill(chars, '\n');
        var strings = lines.stream()
            .map(charList -> charList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining()))
            .collect(toList());
        var rows = Util.unspill(strings, "");
        var recovered = rows.stream()
            .map(row -> row.stream().map(Nsv::unescape).collect(toList()))
            .collect(toList());

        assertEquals(original, recovered);
    }

    @Test
    public void testDecompositionWithEmptyRows() {
        List<List<String>> data = List.of(
            List.of(),
            List.of("a"),
            List.of(),
            List.of()
        );

        String encoded1 = Nsv.encode(data);

        // Via decomposition
        var escaped = data.stream()
            .map(row -> row.stream().map(Nsv::escape).collect(toList()))
            .collect(toList());
        var spilledStrings = Util.spill(escaped, "");
        var spilledChars = Util.spill(
            spilledStrings.stream()
                .map(s -> s.chars().mapToObj(c -> (char)c).collect(toList()))
                .collect(toList()),
            '\n'
        );
        String encoded2 = spilledChars.stream()
            .map(String::valueOf)
            .collect(Collectors.joining());

        assertEquals(encoded1, encoded2);
    }

    @Test
    public void testDecompositionWithEmptyStrings() {
        List<List<String>> data = List.of(
            List.of("", "", ""),
            List.of("a", "", "b")
        );

        String encoded1 = Nsv.encode(data);

        // Via decomposition
        var escaped = data.stream()
            .map(row -> row.stream().map(Nsv::escape).collect(toList()))
            .collect(toList());
        var spilledStrings = Util.spill(escaped, "");
        var spilledChars = Util.spill(
            spilledStrings.stream()
                .map(s -> s.chars().mapToObj(c -> (char)c).collect(toList()))
                .collect(toList()),
            '\n'
        );
        String encoded2 = spilledChars.stream()
            .map(String::valueOf)
            .collect(Collectors.joining());

        assertEquals(encoded1, encoded2);
    }
}