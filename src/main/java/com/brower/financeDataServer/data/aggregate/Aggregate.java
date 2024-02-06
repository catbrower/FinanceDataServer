package com.brower.financeDataServer.data.aggregate;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "aggregates")
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aggregate {
    @Id
    public String id;
    public String ticker;
    public Long t;
    public Double o;
    public Double h;
    public Double l;
    public Double c;
    public Long v;

    public String getField(String fieldName) {
        try {
            return Aggregate.class.getField(fieldName).get(this).toString();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
