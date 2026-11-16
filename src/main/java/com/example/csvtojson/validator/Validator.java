package com.example.csvtojson.validator;

import com.example.csvtojson.config.Config;

import java.util.List;
import java.util.Map;

/**
 * Validates CSV rows against required columns defined in {@link Config}.
 */
public final class Validator {

    private final List<String> requiredColumns;

    public Validator(Config config) {
        this.requiredColumns = config.getRequiredColumns();
    }

    /**
     * Validates a single CSV row.
     *
     * @param row Map where key is column name and value is cell value.
     * @throws ValidationException if validation fails.
     */
    public void validateRow(Map<String, String> row) {
        for (String column : requiredColumns) {
            if (!row.containsKey(column) || row.get(column) == null || row.get(column).isBlank()) {
                throw new ValidationException("Missing required column '" + column + "' in row: " + row);
            }
        }
        // Additional custom validation can be added here (e.g., email format)
    }
}