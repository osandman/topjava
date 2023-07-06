package ru.javawebinar.topjava.service;

import org.slf4j.Logger;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class LogTestExecutionListener implements TestExecutionListener {
    private static final Logger log = getLogger("result");

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        log.info("Active profiles: {}",
                Arrays.stream(testContext.getApplicationContext().getEnvironment().getActiveProfiles()).toList());
        log.info("Starting test {}", testContext.getTestClass());
    }
}
