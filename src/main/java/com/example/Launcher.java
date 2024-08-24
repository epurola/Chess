package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        try {
            logger.info("Starting application...");
            App.main(args);  // App is the JavaFX application class
            logger.info("Application started successfully.");
        } catch (Exception e) {
            logger.error("An error occurred while starting the application", e);
        }
    }
}


