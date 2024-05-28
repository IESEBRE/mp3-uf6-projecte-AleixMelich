package org.amelich.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Aquesta classe s'encarrega d'establir una connexió a la base de dades.
 * Llegeix els detalls de connexió de la base de dades d'un fitxer de propietats i els utilitza per crear una connexió.
 */
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
    /**
     * Aquest mètode s'utilitza per establir una connexió a la base de dades.
     * Utilitza les propietats carregades des del fitxer de propietats per crear una connexió.
     *
     * @return Connexió a la base de dades.
     * @throws SQLException Si es produeix un error d'accés a la base de dades.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }
}
