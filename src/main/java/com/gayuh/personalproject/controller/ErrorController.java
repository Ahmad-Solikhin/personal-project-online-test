package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.util.CustomResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> methodArgumentException(
            MethodArgumentNotValidException exception
    ) {
        log.warn("Error in MethodArgumentNotValidException");

        Map<String, String> errorMap = new HashMap<>();
        exception.getFieldErrors().forEach(error ->
                errorMap.put(error.getField(), error.getDefaultMessage())
        );

        return CustomResponse.generateResponse(
                errorMap,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<Object> responseStatus(
            ResponseStatusException exception
    ) {
        log.warn("Error in ResponseStatusException");

        return CustomResponse.generateResponse(
                exception.getReason(),
                HttpStatus.valueOf(exception.getStatusCode().value())
        );
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolationException(
            ConstraintViolationException exception
    ) {
        log.warn("Error in ConstraintViolationException");
        Map<String, String> errorMap = new HashMap<>();

        exception.getConstraintViolations().forEach(error -> {
            if (errorMap.containsKey(error.getPropertyPath().toString())) {
                String oldMessage = errorMap.get(error.getPropertyPath().toString());
                errorMap.put(
                        error.getPropertyPath().toString(),
                        oldMessage + " \n" + error.getMessage()
                );
            } else {
                errorMap.put(error.getPropertyPath().toString(), error.getMessage());
            }
        });

        return CustomResponse.generateResponse(
                errorMap,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = SizeLimitExceededException.class)
    public ResponseEntity<Object> sizeLimitExceededException(
            SizeLimitExceededException exception
    ) {
        log.warn("Error in SizeLimitExceededException");

        return CustomResponse.generateResponse(
                "size exceeded limit of 5MB",
                HttpStatus.BAD_REQUEST
        );
    }
}
