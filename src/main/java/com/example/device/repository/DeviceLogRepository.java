package com.example.device.repository;

import com.example.device.model.DeviceLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeviceLogRepository extends CrudRepository<DeviceLog, UUID> {
}
