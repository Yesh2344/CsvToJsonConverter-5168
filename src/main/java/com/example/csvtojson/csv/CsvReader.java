package com.example.csvtojson.csv;

import com.example.csvtojson.config.Config;
import com.example.csvtojson.util.LoggerUtil;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Reads CSV files using OpenCSV and returns a lazy {@link Stream} of rows.
 * Each row is represented as a {@link Map} where the key is the column header.
 */
public final class CsvReader implements AutoCloseable {

    private static final Logger LOG = LoggerUtil.getLogger(CsvReader.class);
    private final CSVReader csvReader;
    private final String[] headers;

    public CsvReader(Path csvPath, Config config) throws IOException {
        Reader fileReader = Files.newBufferedReader(csvPath);
        this.csvReader = new CSVReader(fileReader, config.getCsvDelimiter(),
                CSVReader.DEFAULT_QUOTE_CHARACTER,
                CSVReader.DEFAULT_ESCAPE_CHARACTER,
                0, // skipLines = 0 – we will read the header manually
                false,
                false);
        try {
            this.headers = csvReader.readNext();
// small cleanup
            if (headers == null) {
                throw new IllegalArgumentException("CSV file is empty: " + csvPath);
            }
        } catch (CsvValidationException e) {
            throw new IOException("Failed to read CSV header from " + csvPath, e);
        }
        LOG.debug(CsvReader.class, "CSV headers: {}", Arrays.toString(headers));
    }

    /**
     * Returns a {@link Stream} of rows where each row is a {@link Map}
     * of column name → value.
     */
    public Stream<Map<String, String>> streamRows() {
        Iterable<String[]> iterable = () -> new Iterator<>() {
            private String[] nextLine;

            @Override
            public boolean hasNext() {
                try {
                    nextLine = csvReader.readNext();
                    return nextLine != null;
                } catch (IOException | CsvValidationException e) {
                    LOG.error(CsvReader.class, "Error reading CSV line", e);
                    return false;
                }
            }

            @Override
            public String[] next() {
                return nextLine;
            }
        };

        return StreamSupport.stream(iterable.spliterator(), false)
                .map(this::rowToMap);
    }

    private Map<String, String> rowToMap(String[] row) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < headers.length && i < row.length; i++) {
            map.put(headers[i], row[i]);
        }
        // Fill missing columns with empty string
        for (int i = row.length; i < headers.length; i++) {
            map.put(headers[i], "");
        }
        return map;
    }

    @Override
    public void close() throws IOException {
        csvReader.close();
    }
}