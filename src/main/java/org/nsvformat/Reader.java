package org.nsvformat;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Reader implements Iterator<List<String>> {
    private final java.io.Reader reader;
    private final StringBuilder lineBuffer = new StringBuilder();
    private final List<String> rowBuffer = new ArrayList<>();
    private List<String> cachedRow = null;

    private char[] buf = new char[8192];
    private int bufPos = 0;
    private int bufLen = 0;

    public Reader(java.io.Reader reader) {
        this.reader = reader;
    }

    private String tryReadLine() throws IOException {
        while (true) {
            for (int i = bufPos; i < bufLen; i++) {
                if (buf[i] == '\n') {
                    // Line complete, return
                    lineBuffer.append(buf, bufPos, i - bufPos);
                    bufPos = i + 1;
                    String line = lineBuffer.toString();
                    lineBuffer.setLength(0);
                    return line;
                }
            }
            // Keep reading
            lineBuffer.append(buf, bufPos, bufLen - bufPos);
            bufLen = reader.read(buf, 0, buf.length);
            bufPos = 0;
            if (bufLen == -1) {
                // Incomplete line at EOF, preserve lineBuffer for next call
                bufLen = 0;
                return null;
            }
        }
    }

    private List<String> tryReadRow() throws IOException {
        while (true) {
            String line = tryReadLine();
            if (line == null) {
                // Incomplete row at EOF, preserve rowBuffer for next call
                return null;
            }
            if (line.isEmpty()) {
                // Row complete, return
                List<String> row = new ArrayList<>(rowBuffer);
                rowBuffer.clear();
                return row;
            }
            // Cell complete, keep reading
            rowBuffer.add(Nsv.unescape(line));
        }
    }

    @Override
    public boolean hasNext() {
        if (cachedRow == null) {
            try {
                cachedRow = tryReadRow();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return cachedRow != null;
    }

    @Override
    public List<String> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        List<String> result = cachedRow;
        cachedRow = null;
        return result;
    }

    public static Reader fromFile(File file) throws FileNotFoundException {
        return new Reader(new BufferedReader(new FileReader(file)));
    }

    public static Reader fromPath(Path path) throws IOException {
        return new Reader(new BufferedReader(new FileReader(path.toFile())));
    }
}
