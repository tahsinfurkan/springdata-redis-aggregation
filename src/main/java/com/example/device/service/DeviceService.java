package com.example.device.service;

import com.example.device.exception.DeviceBadRequestException;
import com.example.device.exception.DeviceNotFoundException;
import com.example.device.model.Device;
import com.example.device.model.DeviceLog;
import com.example.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
@Profile("redis")
@SuppressWarnings("unchecked")
public class DeviceService {
    private DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
    private int versionCount = 1;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceLogService deviceLogService;

    @Autowired
    private RedisCacheService redisCacheService;

    public List<Device> getDevices() {
        return (List<Device>) deviceRepository.findAll();
    }

    public Optional<Device> getDeviceById(UUID id) {
        Optional<Device> optionalDevice = deviceRepository.findById(id);
        if (optionalDevice.isPresent()) {
            return optionalDevice;
        }
        throw new DeviceNotFoundException("Device not found");
    }

    public void updateDeviceById(Device device, UUID id) {
        Optional<Device> oldDeviceOptional = deviceRepository.findById(id);

        if (!oldDeviceOptional.isPresent()) {
            throw new DeviceNotFoundException("Device not found");
        }

        if (device.getId() != id) {
            deleteDevices(id);
        }
        versionCount += 1;
        device.setVersion(versionCount + ". gen");
        device.setCreatedAt(oldDeviceOptional.get().getCreatedAt());
        device.setUpdatedAt(simple.format(new Date(Instant.now().toEpochMilli())));
        deviceRepository.save(device);
    }

    public void saveDevice(Device device) {
        Optional<Device> optionalDevice = deviceRepository.findById(device.getId());
        if (optionalDevice.isPresent()) {
            throw new DeviceBadRequestException("Device already exist");
        }
        Date now = new Date(Instant.now().toEpochMilli());
        device.setVersion(versionCount + ". gen");
        device.setUpdatedAt(simple.format(now));
        device.setCreatedAt(simple.format(now));
        deviceRepository.save(device);
    }

    public void deleteDevices(UUID id) {
        Optional<Device> optionalDevice = deviceRepository.findById(id);
        if (!optionalDevice.isPresent()) {
            throw new DeviceNotFoundException("Device not found");
        }
        deviceRepository.deleteById(id);
    }

    //Log Side

    public List<DeviceLog> getAllDevicesLog() {
        return deviceLogService.getDevicesLog();
    }

    public Optional<DeviceLog> getDeviceLogById(UUID id) {
        return deviceLogService.getDeviceLogById(id);
    }

    public void saveDeviceLog(DeviceLog deviceLog) {
        deviceLogService.saveDeviceLog(deviceLog);
    }

    //Aggregation side

    public Map getAllDevicesAggr(String id) {
        return deviceLogService.convertAggregationModel(id);
    }

    public void saveDeviceAggr(DeviceLog deviceLog) {
        String deviceId = deviceLog.getDeviceId().toString();
        String deviceAggrId = keyGenerator(deviceId);
        Map<String, String> cache = redisCacheService.getHashMap(deviceAggrId);
        cache = deviceLogService.saveDeviceAggregation(cache, deviceLog);
        redisCacheService.save(deviceAggrId, cache);
    }

    private String keyGenerator(String deviceId) {
        return "Aggregation-" + deviceId;
    }
}