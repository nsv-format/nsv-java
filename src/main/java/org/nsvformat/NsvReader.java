package org.nsvformat;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class NsvReader implements AutoCloseable {
    private static final String META_SEPARATOR = "---";
    private final BufferedReader reader;
    private final List<String> metadata;

    public NsvReader(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public NsvReader(Reader reader) {
        this.reader = new BufferedReader(reader);
        this.metadata = new ArrayList<>();
        parseHeader();
    }

    public List<String> metadata() {
        return List.copyOf(metadata);
    }

    private void parseHeader() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (META_SEPARATOR.equals(line)) {
                    return;
                }
                metadata.add(line);
            }
            throw new IllegalArgumentException("Invalid NSV: missing header separator");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<List<String>> readRow() {
        try {
            var row = new ArrayList<String>();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    return Optional.of(List.copyOf(row));
                }
                row.add(unescape(line));
            }
            
            return row.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(row));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
        reader.close();
    }
}