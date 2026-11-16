package com.example.csvtojson;

import com.example.csvtojson.config.Config;
import com.example.csvtojson.csv.CsvReader;
import com.example.csvtojson.json.JsonWriter;
import com.example.csvtojson.validator.Validator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class CsvToJsonConverterTest {

    private static final String CSV_CONTENT = """
            id,name,email
            1,John Doe,john.doe@example.com
            2,Jane Smith,jane.smith@example.com
            """;

    private Config config;

    @BeforeEach
    void setUp() {
        config = Config.load();
    }

    @Test
    void testSuccessfulConversion(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path csvFile = tempDir.resolve("input.csv");
        Files.writeString(csvFile, CSV_CONTENT);
        Path jsonFile = tempDir.resolve("output.json");

        // Act
        try (CsvReader reader = new CsvReader(csvFile, config)) {
            Validator validator = new Validator(config);
            List<Map<String, String>> rows = reader.streamRows()
                    .peek(row -> validator.validateRow(row))
                    .toList();

            JsonWriter writer = new JsonWriter(config);
            writer.writeJson(rows, jsonFile);
        }

        // Assert
        assertThat(Files).exists(jsonFile);
        String json = Files.readString(jsonFile);
        assertThat(json).contains("\"id\" : \"1\"");
        assertThat(json).contains("\"email\" : \"jane.smith@example.com\"");
    }

    @Test
    void testValidationFailsWhenRequiredColumnMissing(@TempDir Path tempDir) throws IOException {
        // Arrange CSV missing the 'email' column
        String badCsv = """
                id,name
                1,John Doe
                """;
        Path csvFile = tempDir.resolve("bad.csv");
        Files.writeString(csvFile, badCsv);
        Path jsonFile = tempDir.resolve("out.json");

        // Act & Assert
        try (CsvReader reader = new CsvReader(csvFile, config)) {
            Validator validator = new Validator(config);
            List<Map<String, String>> rows = reader.streamRows()
                    .filter(row -> {
                        try {
                            validator.validateRow(row);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .toList();

            assertThat(rows).isEmpty();
        }
    }

    @Test
    void testEmptyCsvFileThrowsException(@TempDir Path tempDir) throws IOException {
        Path emptyCsv = tempDir.resolve("empty.csv");
        Files.createFile(emptyCsv);
        assertThatThrownBy(() -> new CsvReader(emptyCsv, config))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CSV file is empty");
    }
}