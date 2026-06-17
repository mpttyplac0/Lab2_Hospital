package com.example.hospital.model.dao;

import com.example.hospital.util.ConfigManager; // Імпортуємо ConfigManager
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DaoConnection {
    static {
        try {
            String driver = ConfigManager.getProperty("db.driver");
            if (driver == null || driver.trim().isEmpty()) {
                throw new RuntimeException("Database driver property 'db.driver' not found or empty in configuration.");
            }
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found (from ConfigManager): " + ConfigManager.getProperty("db.driver"));
            throw new RuntimeException("Failed to load database driver.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = ConfigManager.getProperty("db.url");
        String user = ConfigManager.getProperty("db.user");
        String password = ConfigManager.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException("Database connection properties (url, user, password) are not fully configured.");
        }

        return DriverManager.getConnection(url, user, password);
    }
}