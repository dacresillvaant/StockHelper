package com.mateusz.springgpt.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String ipAddress = request.getRemoteAddr();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String param = Optional.ofNullable(request.getQueryString()).orElse("");
            String userAgent = request.getHeader("User-Agent");

            log.info("Incoming request from IP: {}, - Method: {}, - URI: {} - User-Agent: {}", ipAddress, method, uri.concat(param), userAgent);

        } catch (Exception e) {
            log.warn("Failed to log request info: ", e);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String ipAddress = request.getRemoteAddr();
        int status = response.getStatus();
        String uri = request.getRequestURI();
        String param = Optional.ofNullable(request.getQueryString()).orElse("");

        log.info("Completed response to IP: {}, URI: {} - Status: {}", ipAddress, uri.concat(param), status);
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        String error = event.getException().getMessage();
        String authenticationDetails = event.getAuthentication().getDetails().toString();

        log.warn("Failed authentication attempt for user: {} - Reason: {},\n details: {}", username, error, authenticationDetails);
    }
}