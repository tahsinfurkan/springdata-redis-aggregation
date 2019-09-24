package com.example.device.exception;

public class DeviceBadRequestException extends RuntimeException{
    public DeviceBadRequestException(String message) {
        super(message);
    }
}
