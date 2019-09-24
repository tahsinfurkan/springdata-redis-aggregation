package com.example.device.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@RedisHash("DevicesLogs")
public class DeviceLog {
    @Id
    private UUID id = UUID.randomUUID();
    private UUID deviceId;
    private String randomUUIDString = id.toString();
    private String ip;
    private String timestamp;
    private String status;
    private BigDecimal humidity;
    private BigDecimal temperature;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal speed;
}
