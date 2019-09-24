package com.example.device.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@RedisHash("LogAggregation")
public class AggregationModel {
    BigDecimal min;
    BigDecimal max;
    BigDecimal sum;
    BigDecimal count;
    BigDecimal avg;

    @JsonGetter("avg")
    BigDecimal getAvg() {
        return sum.divide(count, 2, BigDecimal.ROUND_HALF_UP);
    }

}
