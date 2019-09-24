package com.example.device.exception;

import com.example.device.controller.AbstractController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class DeviceExceptionHandler extends AbstractController {

    @ExceptionHandler(DeviceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Object> handleNotFoundException(HttpServletRequest req, Exception ex) {
        return errorMessageResponse("device_not_found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DeviceBadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(HttpServletRequest req, Exception ex) {
        return errorMessageResponse("device_bad_request", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
