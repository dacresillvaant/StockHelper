package com.mateusz.springgpt.config;

import com.mateusz.springgpt.service.MailgunEmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final MailgunEmailService mailgunEmailService;

    @Autowired
    public GlobalExceptionHandler(MailgunEmailService mailgunEmailService) {
        this.mailgunEmailService = mailgunEmailService;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException exception, HttpServletRequest request) {
        String param = Optional.ofNullable(request.getQueryString()).orElse("");

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", exception.getStatusCode().value());
        responseBody.put("error", exception.getStatusCode());
        responseBody.put("message", exception.getReason());
        responseBody.put("path", request.getRequestURI().concat(param));

        return new ResponseEntity<>(responseBody, exception.getStatusCode());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException exception, HttpServletRequest request) {
        String param = Optional.ofNullable(request.getQueryString()).orElse("");

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", HttpStatus.NOT_FOUND.value());
        responseBody.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        responseBody.put("message", "Static resource not found");
        responseBody.put("path", request.getRequestURI().concat(param));

        log.debug("No static resource found {}", request.getRequestURI().concat(param));
        return new ResponseEntity<>(responseBody, exception.getStatusCode());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationExceptions(Exception exception, HttpServletRequest request) {
        String param = Optional.ofNullable(request.getQueryString()).orElse("");

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", HttpStatus.CONFLICT.value());
        responseBody.put("error", HttpStatus.CONFLICT.getReasonPhrase());
        responseBody.put("message", exception.getMessage());
        responseBody.put("path", request.getRequestURI().concat(param));

        log.warn("Conflict while adding resource", exception);
        return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception exception, HttpServletRequest request) {
        String param = Optional.ofNullable(request.getQueryString()).orElse("");

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseBody.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        responseBody.put("message", exception.getMessage());
        responseBody.put("path", request.getRequestURI().concat(param));

        log.error("Unhandled exception caught in global handler", exception);
        mailgunEmailService.sendErrorAlertEmail(exception, request);

        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}