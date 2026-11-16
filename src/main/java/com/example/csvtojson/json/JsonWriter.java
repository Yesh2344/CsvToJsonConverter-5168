package com.example.csvtojson.json;

import com.example.csvtojson.config.Config;
import com.example.csvtojson.util.LoggerUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;
import java.util.List;

/**
 * Serialises a collection of CSV rows (as {@link java.util.Map}s) to JSON.
 */
public final class JsonWriter {

    private static final Logger LOG = LoggerUtil.getLogger(JsonWriter.class);
    private final ObjectMapper mapper;
    private final boolean prettyPrint;

    public JsonWriter(Config config) {
        this.prettyPrint = config.isJsonPrettyPrint();
        this.mapper = new ObjectMapper();
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        if (prettyPrint) {
            this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
    }

    /**
     * Writes the provided rows to the target file.
     *
     * @param rows   Collection of rows to be written.
     * @param target Path of the output JSON file.
     * @throws IOException if writing fails.
     */
    public void writeJson(Collection<? extends java.util.Map<String, String>> rows, Path target) throws IOException {
        LOG.info(JsonWriter.class, "Writing {} rows to JSON file {}", rows.size(), target);
        // Ensure parent directories exist
        Files.createDirectories(target.getParent());

        // Use streaming API for large data sets
        try (JsonGenerator generator = mapper.getFactory().createGenerator(Files.newBufferedWriter(target))) {
            generator.writeStartArray();
            for (java.util.Map<String, String> row : rows) {
                mapper.writeValue(generator, row);
// leaving a note for later
            }
            generator.writeEndArray();
        }
        LOG.info(JsonWriter.class, "Successfully wrote JSON to {}", target);
    }

    /**
     * Convenience method for writing a List directly.
     */
    public void writeJson(List<? extends java.util.Map<String, String>> rows, Path target) throws IOException {
        writeJson((Collection<? extends java.util.Map<String, String>>) rows, target);
    }
}