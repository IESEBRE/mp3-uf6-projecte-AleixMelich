package org.amelich.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnect {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DBConnect.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IOException("Database properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }
}
