package com.example.device.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@RedisHash("Devices")
public class Device {
    @Id
    private UUID id;
    @NotNull
    @NotEmpty
    private String name;
    private String macAddress;
    private String version;
    private String createdAt;
    private String updatedAt;
}
