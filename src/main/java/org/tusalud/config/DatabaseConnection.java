package org.tusalud.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Dotenv dotenv;

    static {
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            dotenv = null;
        }
    }

    private static String getEnv(String key, String defaultValue) {
        if (dotenv != null) {
            String val = dotenv.get(key);
            if (val != null && !val.trim().isEmpty()) {
                return val;
            }
        }
        String sysVal = System.getenv(key);
        if (sysVal != null && !sysVal.trim().isEmpty()) {
            return sysVal;
        }
        return defaultValue;
    }

    public static Connection getConnectionSinBD() throws SQLException {
        String host = getEnv("DB_HOST", "localhost");
        String port = getEnv("DB_PORT", "3306");
        String user = getEnv("DB_USER", "root");
        String pass = getEnv("DB_PASSWORD", "");

        String url = String.format("jdbc:mysql://%s:%s/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port);
        return DriverManager.getConnection(url, user, pass);
    }

    public static Connection getConnection() throws SQLException {
        String host = getEnv("DB_HOST", "localhost");
        String port = getEnv("DB_PORT", "3306");
        String name = getEnv("DB_NAME", "tusalud");
        String user = getEnv("DB_USER", "root");
        String pass = getEnv("DB_PASSWORD", "");

        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, name);
        return DriverManager.getConnection(url, user, pass);
    }
}
