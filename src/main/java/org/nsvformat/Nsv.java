package org.nsvformat;

import java.io.*;
import java.util.Collection;
import java.util.List;

public final class Nsv {
    private Nsv() {}

    public static NsvData read(InputStream inputStream) {
        try (var reader = new NsvReader(inputStream)) {
            var metadata = reader.metadata();
            var rows = reader.rows().toList();
            return new NsvData(metadata, rows);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static NsvData read(Reader reader) {
        try (var nsvReader = new NsvReader(reader)) {
            var metadata = nsvReader.metadata();
            var rows = nsvReader.rows().toList();
            return new NsvData(metadata, rows);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static NsvData read(String content) {
        return read(new StringReader(content));
    }

    public static void write(Collection<? extends Collection<String>> rows, OutputStream outputStream) {
        write(rows, outputStream, List.of());
    }

    public static void write(Collection<? extends Collection<String>> rows, OutputStream outputStream, Collection<String> metadata) {
        try (var writer = NsvWriter.withMetadata(outputStream, metadata)) {
            writer.writeRows(rows);
        }
    }

    public static void write(Collection<? extends Collection<String>> rows, Writer writer) {
        write(rows, writer, List.of());
    }

    public static void write(Collection<? extends Collection<String>> rows, Writer writer, Collection<String> metadata) {
        try (var nsvWriter = NsvWriter.withMetadata(writer, metadata)) {
            nsvWriter.writeRows(rows);
        }
    }

    public static String write(Collection<? extends Collection<String>> rows) {
        return write(rows, List.of());
    }

    public static String write(Collection<? extends Collection<String>> rows, Collection<String> metadata) {
        var stringWriter = new StringWriter();
        write(rows, stringWriter, metadata);
        return stringWriter.toString();
    }
}