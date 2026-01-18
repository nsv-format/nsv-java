package org.nsvformat;

import java.util.ArrayList;
import java.util.List;

public final class Nsv {
    private Nsv() {}

    public static List<List<String>> decode(String s) {
        List<List<String>> data = new ArrayList<>();
        List<String> row = new ArrayList<>();
        int start = 0;

        for (int pos = 0; pos < s.length(); pos++) {
            char c = s.charAt(pos);
            if (c == '\n') {
                if (pos - start >= 1) {
                    row.add(unescape(s.substring(start, pos)));
                } else {
                    data.add(row);
                    row = new ArrayList<>();
                }
                start = pos + 1;
            }
        }

        return data;
    }

    public static String encode(List<List<String>> data) {
        List<String> lines = new ArrayList<>();
        for (List<String> row : data) {
            for (String cell : row) {
                lines.add(escape(cell));
            }
            lines.add("");
        }

        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(line).append("\n");
        }
        return result.toString();
    }
    
    public static String escape(String s) {
        if (s.isEmpty()) {
            return "\\";
        }
        if (s.contains("\n") || s.contains("\\")) {
            return s.replace("\\", "\\\\").replace("\n", "\\n");
        }
        return s;
    }
    
    public static String unescape(String s) {
        if (s.equals("\\")) {
            return "";
        }
        if (!s.contains("\\")) {
            return s;
        }

        StringBuilder out = new StringBuilder();
        boolean escaped = false;
        for (char c : s.toCharArray()) {
            if (escaped) {
                if (c == 'n') {
                    out.append('\n');
                } else if (c == '\\') {
                    out.append('\\');
                } else {
                    out.append('\\');
                    out.append(c);
                }
                escaped = false;
            } else {
                if (c == '\\') {
                    escaped = true;
                } else {
                    out.append(c);
                }
            }
        }
        return out.toString();
    }
}