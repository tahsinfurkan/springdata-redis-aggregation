package com.example.device.controller;

import com.example.device.model.Device;
import com.example.device.model.DeviceLog;
import com.example.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
@Profile("redis")
@RequestMapping("/devices")
public class DeviceController extends AbstractController {

    @Autowired
    private DeviceService deviceService;

    //Device side

    @GetMapping("/")
    public ResponseEntity<Object> getAllDevices() {
        return getResponse(deviceService.getDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDeviceById(@PathVariable(name = "id") UUID id) {
        return getResponse(deviceService.getDeviceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDeviceById(@RequestBody Device device, @PathVariable UUID id) {
        deviceService.updateDeviceById(device, id);
        return successMessageResponse("Device Update Success");
    }

    @PostMapping("/")
    public ResponseEntity<Object> saveDevice(@RequestBody @Valid Device device) {
        deviceService.saveDevice(device);
        return successMessageResponse("Device Save Success");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDeviceById(@PathVariable UUID id) {
        deviceService.deleteDevices(id);
        return successMessageResponse("Device Delete Success");
    }

    //Log side

    @GetMapping("/logs")
    public ResponseEntity<Object> getAllDevicesLog() {
        return getResponse(deviceService.getAllDevicesLog());
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<Object> getDeviceLogById(@PathVariable(name = "id") UUID id) {
        return getResponse(deviceService.getDeviceLogById(id));
    }

    @PostMapping("/logs")
    public ResponseEntity<Object> saveDeviceLog(@RequestBody @Valid DeviceLog deviceLog) {
        deviceService.saveDeviceLog(deviceLog);
        return successMessageResponse("Device Log Save Success");
    }

    //Aggregation side

    @GetMapping("/{id}/logs/aggr")
    public ResponseEntity<Object> getAllDeviceAggr(@PathVariable(name = "id") UUID id) {
        return getResponse(deviceService.getAllDevicesAggr(id.toString()));
    }

    @PostMapping("/logs/aggr")
    public ResponseEntity<Object> saveDevicesAggr(@RequestBody @Valid DeviceLog deviceLog) {
        deviceService.saveDeviceAggr(deviceLog);
        return successMessageResponse("Device Log Aggregation Save Success");
    }
}