package com.example.device.service;

import com.example.device.exception.DeviceBadRequestException;
import com.example.device.exception.DeviceNotFoundException;
import com.example.device.model.AggregationModel;
import com.example.device.model.DeviceLog;
import com.example.device.repository.DeviceLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
@Profile("redis")
@SuppressWarnings("unchecked")
public class DeviceLogService {

    @Autowired
    private DeviceLogRepository deviceLogRepository;

    @Autowired
    private RedisCacheService redisCacheService;

    private AggregationModel aggregationModel = new AggregationModel();

    private DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
    //aggr side

    public Map<String, String> saveDeviceAggregation(Map<String, String> aggregation, DeviceLog deviceLog) {
        Map<String, BigDecimal> aggregationMap = new HashMap<>();
        aggregationMap.put("humidity", deviceLog.getHumidity());
        aggregationMap.put("temperature", deviceLog.getTemperature());
        aggregationMap.put("latitude", deviceLog.getLatitude());
        aggregationMap.put("longitude", deviceLog.getLongitude());
        aggregationMap.put("speed", deviceLog.getSpeed());
        Map<String, String> deviceLogMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String deviceAggregationStr = null;
        if (aggregation.isEmpty()) {
            for (Map.Entry<String, BigDecimal> entry : aggregationMap.entrySet()) {
                String key = entry.getKey();
                BigDecimal value = entry.getValue();
                aggregationModel.setMin(value);
                aggregationModel.setMax(value);
                aggregationModel.setSum(value);
                aggregationModel.setCount(BigDecimal.ONE);
                try {
                    deviceAggregationStr = objectMapper.writeValueAsString(aggregationModel);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                deviceLogMap.put(key, deviceAggregationStr);
            }
            return deviceLogMap;
        }

        for (Map.Entry<String, BigDecimal> entry : aggregationMap.entrySet()) {
            String key = entry.getKey();
            BigDecimal value = entry.getValue();
            String jsonString = aggregation.get(key);
            Map<String, AggregationModel> modelMap = new HashMap<>();
            try {
                AggregationModel aggregationModel = objectMapper.readValue(jsonString, AggregationModel.class);
                modelMap.put(key, aggregationModel);
            } catch (IOException e) {
                e.printStackTrace();
            }

            BigDecimal min = modelMap.get(key).getMin();
            BigDecimal max = modelMap.get(key).getMax();
            BigDecimal sum = modelMap.get(key).getSum();
            BigDecimal count = modelMap.get(key).getCount();
            if (min.compareTo(value) > 0) {
                aggregationModel.setMin(value);
            }
            aggregationModel.setMin(min);
            if (max.compareTo(value) < 0) {
                aggregationModel.setMax(value);
            }
            aggregationModel.setMax(max);
            aggregationModel.setSum(sum.add(value));
            aggregationModel.setCount(count.add(BigDecimal.ONE));
            try {
                deviceAggregationStr = objectMapper.writeValueAsString(aggregationModel);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            deviceLogMap.put(key, deviceAggregationStr);
        }
        return deviceLogMap;
    }

    public Map<String, AggregationModel> convertAggregationModel(String id) {
        Map<String, String> JSONMap = redisCacheService.getHashMap(keyGenerator(id));
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, AggregationModel> aggregationMap = new HashMap<>();
        for (Map.Entry<String, String> entry : JSONMap.entrySet()) {
            String key = entry.getKey();
            String jsonString = JSONMap.get(key);
            try {
                AggregationModel aggregationModel = objectMapper.readValue(jsonString, AggregationModel.class);
                aggregationMap.put(key, aggregationModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return aggregationMap;
    }

    public List<DeviceLog> getDevicesLog() {
        return (List<DeviceLog>) deviceLogRepository.findAll();
    }

    public Optional<DeviceLog> getDeviceLogById(UUID id) {
        Optional<DeviceLog> optionalDeviceLog = deviceLogRepository.findById(id);
        if (optionalDeviceLog.isPresent()) {
            return optionalDeviceLog;
        }
        throw new DeviceNotFoundException("Device log not found");
    }

    public void saveDeviceLog(DeviceLog deviceLog) {
        Optional<DeviceLog> optionalDeviceLog = deviceLogRepository.findById(deviceLog.getId());
        if (optionalDeviceLog.isPresent()) {
            throw new DeviceBadRequestException("Device log already exist");
        }
        deviceLog.setTimestamp(simple.format(new Date(Instant.now().toEpochMilli())));
        deviceLogRepository.save(deviceLog);
    }

    private String keyGenerator(String deviceId) {
        return "Aggregation-" + deviceId;
    }

}
