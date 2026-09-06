package org.nsvformat;

import java.io.*;
import java.util.*;

public class ReaderBenchmark {

    static class OldReader implements Iterator<List<String>> {
        private final java.io.Reader reader;
        private final StringBuilder lineBuffer = new StringBuilder();
        private final List<String> rowBuffer = new ArrayList<>();
        private List<String> cachedRow = null;

        OldReader(java.io.Reader reader) { this.reader = reader; }

        private String tryReadLine() throws IOException {
            while (true) {
                int c = reader.read();
                if (c == -1) return null;
                if (c == '\n') {
                    String line = lineBuffer.toString();
                    lineBuffer.setLength(0);
                    return line;
                }
                lineBuffer.append((char) c);
            }
        }

        private List<String> tryReadRow() throws IOException {
            while (true) {
                String line = tryReadLine();
                if (line == null) return null;
                if (line.isEmpty()) {
                    List<String> row = new ArrayList<>(rowBuffer);
                    rowBuffer.clear();
                    return row;
                }
                rowBuffer.add(Nsv.unescape(line));
            }
        }

        @Override public boolean hasNext() {
            if (cachedRow == null) {
                try { cachedRow = tryReadRow(); }
                catch (IOException e) { throw new UncheckedIOException(e); }
            }
            return cachedRow != null;
        }

        @Override public List<String> next() {
            if (!hasNext()) throw new NoSuchElementException();
            List<String> result = cachedRow;
            cachedRow = null;
            return result;
        }
    }

    static String generateData(int rows, int colsPerRow, int fieldLen) {
        StringBuilder sb = new StringBuilder();
        Random rng = new Random(42);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < colsPerRow; c++) {
                for (int i = 0; i < fieldLen; i++) {
                    char ch = (char) ('a' + rng.nextInt(26));
                    sb.append(ch);
                }
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    static int drain(Iterator<List<String>> it) {
        int count = 0;
        while (it.hasNext()) { it.next(); count++; }
        return count;
    }

    static long benchOld(String data, int iters) {
        for (int i = 0; i < iters / 2; i++)
            drain(new OldReader(new StringReader(data)));

        long t0 = System.nanoTime();
        for (int i = 0; i < iters; i++)
            drain(new OldReader(new StringReader(data)));
        return System.nanoTime() - t0;
    }

    static long benchNew(String data, int iters) {
        for (int i = 0; i < iters / 2; i++)
            drain(new Reader(new StringReader(data)));

        long t0 = System.nanoTime();
        for (int i = 0; i < iters; i++)
            drain(new Reader(new StringReader(data)));
        return System.nanoTime() - t0;
    }

    static long benchDecode(String data, int iters) {
        for (int i = 0; i < iters / 2; i++)
            Nsv.decode(data);

        long t0 = System.nanoTime();
        for (int i = 0; i < iters; i++)
            Nsv.decode(data);
        return System.nanoTime() - t0;
    }

    static long benchOldBuffered(String data, int iters) {
        for (int i = 0; i < iters / 2; i++)
            drain(new OldReader(new BufferedReader(new StringReader(data))));

        long t0 = System.nanoTime();
        for (int i = 0; i < iters; i++)
            drain(new OldReader(new BufferedReader(new StringReader(data))));
        return System.nanoTime() - t0;
    }

    public static void main(String[] args) {
        int[][] scenarios = {
            // rows, cols, fieldLen, iters
            {100,   5,   10,   5000},
            {100,   5,   200,  2000},
            {1000,  10,  50,   500},
            {10000, 5,   20,   100},
        };

        System.out.printf("%-35s %12s %12s %12s %12s %8s%n",
            "scenario", "old(raw)", "old(buf)", "new", "decode", "speedup");
        System.out.println("-".repeat(95));

        for (int[] s : scenarios) {
            int rows = s[0], cols = s[1], flen = s[2], iters = s[3];
            String data = generateData(rows, cols, flen);
            String label = rows + "r x " + cols + "c x " + flen + "ch (" + (data.length()/1024) + "KB)";

            long tOldRaw = benchOld(data, iters);
            long tOldBuf = benchOldBuffered(data, iters);
            long tNew    = benchNew(data, iters);
            long tDecode = benchDecode(data, iters);

            double msOldRaw = tOldRaw / 1e6;
            double msOldBuf = tOldBuf / 1e6;
            double msNew    = tNew / 1e6;
            double msDecode = tDecode / 1e6;

            System.out.printf("%-35s %10.1f ms %10.1f ms %10.1f ms %10.1f ms %7.2fx%n",
                label, msOldRaw, msOldBuf, msNew, msDecode, msOldBuf / msNew);
        }
    }
}
