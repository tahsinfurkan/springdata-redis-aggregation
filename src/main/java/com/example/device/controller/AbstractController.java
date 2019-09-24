package com.example.device.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public abstract class AbstractController {
    ResponseEntity<Object> successMessageResponse(String message) {
        HashMap<String, String> map = new HashMap<>();
        map.put("message", message);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    protected ResponseEntity<Object> errorMessageResponse(String message, String description, HttpStatus status) {
        HashMap<String, String> map = new HashMap<>();
        map.put("error", message);
        map.put("error_description", description);
        return new ResponseEntity<>(map, status);
    }

    ResponseEntity<Object> getResponse(Object obj) {
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

}