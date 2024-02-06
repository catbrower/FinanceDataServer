package com.brower.financeDataServer.data.aggregate;

import java.util.List;
import java.util.stream.Collectors;

public class AggregateEncoder {
    public static List<String> HEADERS = List.of("ticker", "t", "o", "h", "l", "c", "v");

    public static String encode(Aggregate aggregate) throws RuntimeException {
        return HEADERS
                .stream()
                .map(aggregate::getField)
                .collect(Collectors.joining(","));
    }
}
