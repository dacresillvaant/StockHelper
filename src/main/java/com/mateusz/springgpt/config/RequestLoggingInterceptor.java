package com.mateusz.springgpt.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String ipAddress = request.getRemoteAddr();
            String method = request.getMethod();
            String uri = request.getRequestURI();

            log.info("Incoming request from IP: {}, - Method: {}, - URI: {}", ipAddress, method, uri);

        } catch (Exception e) {
            log.warn("Failed to log request info: ", e);
        }
        return true;
    }
}