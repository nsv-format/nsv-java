package org.nsvformat;

import java.io.*;
import java.util.Collection;
import java.util.List;

public class NsvWriter implements AutoCloseable {
    private static final String META_SEPARATOR = "---";
    private final PrintWriter writer;
    private boolean headerWritten = false;

    public NsvWriter(OutputStream outputStream) {
        this(new OutputStreamWriter(outputStream));
    }

    public NsvWriter(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    public static NsvWriter withMetadata(Writer writer, Collection<String> metadata) {
        var nsvWriter = new NsvWriter(writer);
        nsvWriter.writeHeader(metadata);
        return nsvWriter;
    }

    public static NsvWriter withMetadata(OutputStream outputStream, Collection<String> metadata) {
        return withMetadata(new OutputStreamWriter(outputStream), metadata);
    }

    private void writeHeader(Collection<String> metadata) {
        if (headerWritten) {
            throw new IllegalStateException("Header already written");
        }
        
        metadata.forEach(writer::println);
        writer.println(META_SEPARATOR);
        headerWritten = true;
    }

    private void ensureHeader() {
        if (!headerWritten) {
            writer.println(META_SEPARATOR);
            headerWritten = true;
        }
    }

    public NsvWriter writeRow(Collection<String> row) {
        ensureHeader();
        
        for (var cell : row) {
            writer.println(escape(cell));
        }
        writer.println();
        
        return this;
    }

    public NsvWriter writeRows(Collection<? extends Collection<String>> rows) {
        rows.forEach(this::writeRow);
        return this;
    }

    public static String escape(String s) {
        if (s == null || s.isEmpty()) {
            return "\\";
        }
        return s.replace("\\", "\\\\").replace("\n", "\\n");
    }

    @Override
    public void close() {
        writer.close();
    }
}