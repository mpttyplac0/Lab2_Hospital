package com.example.hospital.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Properties properties = new Properties();
    private static final String PROPERTIES_FILE_NAME = "db.properties";

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + PROPERTIES_FILE_NAME + " in classpath.");
                throw new RuntimeException("Configuration file '" + PROPERTIES_FILE_NAME + "' not found in classpath.");
            }
            properties.load(input);
        } catch (IOException ex) {
            System.err.println("Error loading properties file '" + PROPERTIES_FILE_NAME + "': " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error loading properties file '" + PROPERTIES_FILE_NAME + "'.", ex);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

}