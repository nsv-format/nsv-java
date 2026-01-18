package org.nsvformat;

import java.io.*;
import java.nio.file.Path;

public class Writer {
    private final java.io.Writer writer;

    public Writer(java.io.Writer writer) {
        this.writer = writer;
    }

    public void writeRow(Iterable<String> row) throws IOException {
        for (String cell : row) {
            writer.write(Nsv.escape(cell));
            writer.write('\n');
        }
        writer.write('\n');
    }

    public void writeRows(Iterable<? extends Iterable<String>> rows) throws IOException {
        for (Iterable<String> row : rows) {
            writeRow(row);
        }
    }

    public static Writer fromFile(File file) throws IOException {
        return new Writer(new BufferedWriter(new FileWriter(file)));
    }

    public static Writer fromPath(Path path) throws IOException {
        return new Writer(new BufferedWriter(new FileWriter(path.toFile())));
    }
}
