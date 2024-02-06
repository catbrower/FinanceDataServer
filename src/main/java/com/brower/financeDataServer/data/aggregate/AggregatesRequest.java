package com.brower.financeDataServer.data.aggregate;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AggregatesRequest {
    private List<String> tickers;
    private String timespan;
    private Integer multiplier;
    private Long from;
    private Long to;
    private boolean isInvalid;
}
