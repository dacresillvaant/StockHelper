package com.mateusz.springgpt.utils;

import lombok.extern.slf4j.Slf4j;
import org.testng.ITestListener;
import org.testng.ITestResult;

@Slf4j
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        log.info("##### Test started: " + result.getMethod().getMethodName());
    }
}
