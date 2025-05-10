package com.mateusz.springgpt.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException exception, HttpServletRequest request) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", exception.getStatusCode().value());
        responseBody.put("error", exception.getStatusCode());
        responseBody.put("message", exception.getReason());
        responseBody.put("path", request.getRequestURI().concat(request.getQueryString()));
        return new ResponseEntity<>(responseBody, exception.getStatusCode());
    }
}