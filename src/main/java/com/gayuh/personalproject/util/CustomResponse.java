package com.gayuh.personalproject.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class CustomResponse {

    private static Map<String, Object> response;

    public static ResponseEntity<Object> generateResponse(
            String message,
            HttpStatus status,
            Object data
    ){
        response = new HashMap<>(Map.of(
                "message", message,
                "status", status.value(),
                "data", data
        ));

        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<Object> generateResponse(
            Object message,
            HttpStatus status
    ){
        response = new HashMap<>(Map.of(
                "message", message,
                "status", status.value()
        ));

        return ResponseEntity.status(status).body(response);
    }
}
