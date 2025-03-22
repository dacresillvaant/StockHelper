package com.mateusz.springgpt.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class ScheduledTaskLogger {

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object logScheduledExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        log.info("Starting scheduled task: {}", methodName);
        try {
            Object result = joinPoint.proceed(); // Executes the actual method
            log.info("Finished scheduled task: {}", methodName);
            return result;
        } catch (Exception e) {
            log.error("Scheduled task {} failed: {}", methodName, e.getMessage());
            throw e;
        }
    }
}