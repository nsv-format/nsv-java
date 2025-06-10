package org.nsvformat;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class NsvReader implements AutoCloseable {
    private static final String META_SEPARATOR = "---";
    private final Scanner scanner;
    private final List<String> metadata;

    public NsvReader(InputStream inputStream) {
        this(new InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8));
    }

    public NsvReader(Reader reader) {
        this.scanner = new Scanner(reader);
        this.scanner.useDelimiter("\n");
        this.metadata = new ArrayList<>();
        parseHeader();
    }

    public List<String> metadata() {
        return List.copyOf(metadata);
    }

    private void parseHeader() {
        String line;
        while (scanner.hasNext()) {
            line = scanner.next();
            if (META_SEPARATOR.equals(line)) {
                return;
            }
            metadata.add(line);
        }
        throw new IllegalArgumentException("Invalid NSV: missing header separator");
    }

    public Optional<List<String>> readRow() {
        var row = new ArrayList<String>();
        String line;
        
        while (scanner.hasNext()) {
            line = scanner.next();
            if (line.isEmpty()) {
                return Optional.of(List.copyOf(row));
            }
            row.add(unescape(line));
        }
        
        return row.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(row));
    }

    public Stream<List<String>> rows() {
        return Stream.generate(this::readRow)
                     .takeWhile(Optional::isPresent)
                     .map(Optional::get);
    }

    public static String unescape(String s) {
        return "\\".equals(s) ? "" : s.replace("\\n", "\n").replace("\\\\", "\\");
    }

    @Override
    public void close() throws IOException {
        scanner.close();
    }
}