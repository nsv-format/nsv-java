package org.nsvformat;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ResumableTest {

    // Simulates a stream where data can be added dynamically
    static class TestResumableReader extends java.io.Reader {
        private final StringBuilder buffer = new StringBuilder();
        private int pos = 0;

        public void addData(String data) {
            buffer.append(data);
        }

        @Override
        public int read() {
            if (pos < buffer.length()) {
                return buffer.charAt(pos++);
            } else {
                return -1; // EOF for now
            }
        }

        @Override
        public int read(char[] cbuf, int off, int len) {
            if (len == 0) return 0;
            int c = read();
            if (c == -1) return -1;
            cbuf[off] = (char) c;
            return 1;
        }

        @Override
        public void close() {}
    }

    @Test
    public void testResumeAfterPartialLineAtEof() {
        var stream = new TestResumableReader();
        stream.addData("a\nb");  // incomplete line
        var reader = new Reader(stream);

        assertFalse(reader.hasNext(), "No complete row yet");

        stream.addData("c\n\n");  // completes cell and row
        assertTrue(reader.hasNext(), "Row completed after reading more");

        var row = reader.next();
        assertEquals(List.of("a", "bc"), row);
    }

    @Test
    public void testResumeAfterPartialRowAtEof() {
        var stream = new TestResumableReader();
        stream.addData("a\nb\n");  // one complete cell
        var reader = new Reader(stream);

        assertFalse(reader.hasNext(), "No complete row yet");

        stream.addData("c\n\n");  // second cell and row terminator
        assertTrue(reader.hasNext(), "Row completed after reading more");

        var row = reader.next();
        assertEquals(List.of("a", "b", "c"), row);
    }

    @Test
    public void testResumeAcrossMultipleEofs() {
        var stream = new TestResumableReader();
        stream.addData("a");
        var reader = new Reader(stream);

        assertFalse(reader.hasNext(), "Incomplete line");

        stream.addData("\n");
        assertFalse(reader.hasNext(), "One cell, row incomplete");

        stream.addData("b");
        assertFalse(reader.hasNext(), "Still incomplete");

        stream.addData("\n\n");
        assertTrue(reader.hasNext(), "Row complete");

        var row = reader.next();
        assertEquals(List.of("a", "b"), row);
    }

    @Test
    public void testMultipleRowsWithResumption() {
        var stream = new TestResumableReader();
        stream.addData("a\n\n");
        var reader = new Reader(stream);

        assertTrue(reader.hasNext());
        assertEquals(List.of("a"), reader.next());

        stream.addData("b\n");
        assertFalse(reader.hasNext(), "Second row incomplete");

        stream.addData("\n");
        assertTrue(reader.hasNext(), "Second row complete");
        assertEquals(List.of("b"), reader.next());

        assertFalse(reader.hasNext());
    }

    @Test
    public void testEmptyRowWithResumption() {
        var stream = new TestResumableReader();
        stream.addData("\n");
        var reader = new Reader(stream);

        // Single \n is actually a complete empty row (empty line = row terminator)
        assertTrue(reader.hasNext(), "Empty row complete");
        assertEquals(List.of(), reader.next());

        // Second \n would be another empty row, but we need to add it first
        assertFalse(reader.hasNext(), "No more complete rows");
        stream.addData("\n");
        assertTrue(reader.hasNext(), "Second empty row complete");
        assertEquals(List.of(), reader.next());
    }

    @Test
    public void testEscapedContentWithResumption() {
        var stream = new TestResumableReader();
        stream.addData("hel\\n");  // "hel\\nlo" split mid-escape
        var reader = new Reader(stream);

        assertFalse(reader.hasNext());

        stream.addData("lo\n\n");
        assertTrue(reader.hasNext());
        assertEquals(List.of("hel\nlo"), reader.next());
    }

    @Test
    public void testCompleteRowArrivesAtOnce() {
        var stream = new TestResumableReader();
        stream.addData("a\nb\n\n");
        var reader = new Reader(stream);

        assertTrue(reader.hasNext());
        assertEquals(List.of("a", "b"), reader.next());

        stream.addData("c\n\n");
        assertTrue(reader.hasNext());
        assertEquals(List.of("c"), reader.next());
    }
}
