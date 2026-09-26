package org.example.tbc_swagger_api_project.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Framework configuration loaded from {@code config.properties}.
 * Any key can be overridden with a JVM system property, e.g. {@code -Dbase.uri=http://localhost:8080/v2}.
 */
public final class ApiConfig {

    private static final Properties PROPERTIES = load();

    private ApiConfig() {
    }

    public static String baseUri() {
        return get("base.uri");
    }

    public static String apiKey() {
        return get("api.key");
    }

    public static boolean loggingEnabled() {
        return Boolean.parseBoolean(get("api.logging.enabled"));
    }

    /** How many times to re-check a resource the shared public server has not propagated yet. */
    public static int pollAttempts() {
        return Integer.parseInt(get("poll.attempts"));
    }

    public static long pollIntervalMillis() {
        return Long.parseLong(get("poll.interval.millis"));
    }

    private static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null) {
            throw new IllegalStateException("Missing configuration property: " + key);
        }
        return value.trim();
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = ApiConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return properties;
    }
}
