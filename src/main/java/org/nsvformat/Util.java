package org.nsvformat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Util {
    private Util() {}

    public static <T> List<T> spill(Iterable<? extends Iterable<T>> seqseq, T marker) {
        List<T> seq = new ArrayList<>();
        for (Iterable<T> row : seqseq) {
            for (T item : row) {
                seq.add(item);
            }
            seq.add(marker);
        }
        return seq;
    }

    public static <T> List<List<T>> unspill(Iterable<T> seq, T marker) {
        List<List<T>> seqseq = new ArrayList<>();
        List<T> row = new ArrayList<>();
        for (T item : seq) {
            if (!Objects.equals(item, marker)) {
                row.add(item);
            } else {
                seqseq.add(row);
                row = new ArrayList<>();
            }
        }
        return seqseq;
    }
}
