package com.test.libraryexample;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupLogger {
    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    @PostConstruct
    public void logStartup() {
        log.info("✅ Library has started successfully!");
    }
}
