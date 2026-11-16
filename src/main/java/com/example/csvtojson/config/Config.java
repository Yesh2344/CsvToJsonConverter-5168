package com.example.csvtojson.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Loads configuration from {@code application.properties} and overrides
 * with environment variables prefixed with {@code CSV2JSON_}.
 */
public final class Config {

    private static final String ENV_PREFIX = "CSV2JSON_";
    private static final String PROPERTIES_FILE = "/application.properties";

    private final Properties properties = new Properties();

    // Cached values for quick access
    private final char csvDelimiter;
    private final List<String> requiredColumns;
    private final boolean jsonPrettyPrint;
    private final String loggingLevelRoot;

    private Config() {
        loadFromFile();
        overrideWithEnv();

        this.csvDelimiter = properties.getProperty("csv.delimiter", ",").charAt(0);
        this.requiredColumns = Arrays.stream(
                properties.getProperty("csv.requiredColumns", "")
        ).map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        this.jsonPrettyPrint = Boolean.parseBoolean(
                properties.getProperty("json.prettyPrint", "true")
        );
        this.loggingLevelRoot = properties.getProperty("logging.level.root", "INFO");
    }

    private void loadFromFile() {
        try (InputStream is = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load configuration from " + PROPERTIES_FILE, e);
        }
    }

    private void overrideWithEnv() {
        Map<String, String> env = System.getenv();
        env.forEach((key, value) -> {
            if (key.startsWith(ENV_PREFIX)) {
                String propKey = key.substring(ENV_PREFIX.length())
                        .toLowerCase()
                        .replace('_', '.');
                properties.setProperty(propKey, value);
            }
        });
    }

    public static Config load() {
        return new Config();
    }

    public char getCsvDelimiter() {
        return csvDelimiter;
    }

    public List<String> getRequiredColumns() {
        return Collections.unmodifiableList(requiredColumns);
    }

    public boolean isJsonPrettyPrint() {
        return jsonPrettyPrint;
    }

    public String getLoggingLevelRoot() {
        return loggingLevelRoot;
    }

    // Additional helper for external callers
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}