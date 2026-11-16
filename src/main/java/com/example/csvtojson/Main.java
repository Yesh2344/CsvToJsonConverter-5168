package com.example.csvtojson;

import com.example.csvtojson.config.Config;
import com.example.csvtojson.csv.CsvReader;
import com.example.csvtojson.json.JsonWriter;
import com.example.csvtojson.validator.ValidationException;
import com.example.csvtojson.validator.Validator;
import com.example.csvtojson.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entry point for the CsvToJsonConverter CLI application.
 *
 * Usage:
 * java -jar CsvToJsonConverter.jar --input=path/to/file.csv --output=path/to/file.json
 */
public class Main {

    private static final Logger LOG = LoggerUtil.getLogger(Main.class);

    public static void main(String[] args) {
        Config config = Config.load();
        configureLogging(config.getLoggingLevelRoot());

        Map<String, String> cliOptions = parseArgs(args);
        Path inputPath = resolvePath(cliOptions.getOrDefault("input", System.getenv("CSV2JSON_INPUT_PATH")));
        Path outputPath = resolvePath(cliOptions.getOrDefault("output", System.getenv("CSV2JSON_OUTPUT_PATH")));

        if (inputPath == null || outputPath == null) {
            LOG.error(Main.class, "Both --input and --output arguments (or corresponding environment variables) are required.", null);
            System.exit(1);
        }

        LOG.info(Main.class, "Starting conversion: {} -> {}", inputPath, outputPath);
        try (CsvReader csvReader = new CsvReader(inputPath, config)) {
            Validator validator = new Validator(config);
            List<Map<String, String>> validRows = csvReader.streamRows()
                    .peek(row -> LOG.debug(Main.class, "Read row: {}", row))
                    .filter(row -> {
                        try {
                            validator.validateRow(row);
                            return true;
                        } catch (ValidationException ve) {
                            LOG.error(Main.class, "Validation error: " + ve.getMessage(), ve);
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

            JsonWriter jsonWriter = new JsonWriter(config);
            jsonWriter.writeJson(validRows, outputPath);
            LOG.info(Main.class, "Conversion completed successfully.");
        } catch (IOException e) {
            LOG.error(Main.class, "IO error during conversion", e);
            System.exit(2);
        } catch (Exception e) {
            LOG.error(Main.class, "Unexpected error", e);
            System.exit(3);
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] kv = arg.substring(2).split("=", 2);
                if (kv.length == 2) {
                    map.put(kv[0].toLowerCase(), kv[1]);
                }
            }
        }
        return map;
    }

    private static Path resolvePath(String pathStr) {
        if (pathStr == null || pathStr.isBlank()) {
            return null;
        }
        return Paths.get(pathStr).toAbsolutePath().normalize();
    }

    private static void configureLogging(String level) {
        // Logback picks up configuration from logback.xml if present.
        // Here we programmatically set the root level if needed.
        ch.qos.logback.classic.Logger rootLogger =
                (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        ch.qos.logback.classic.Level logLevel = ch.qos.logback.classic.Level.toLevel(level, ch.qos.logback.classic.Level.INFO);
        rootLogger.setLevel(logLevel);
        LOG.info(Main.class, "Logging level set to {}", logLevel);
    }
}