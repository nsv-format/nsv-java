package org.nsvformat;

import java.util.List;

public record NsvData(List<String> metadata, List<List<String>> rows) {
}