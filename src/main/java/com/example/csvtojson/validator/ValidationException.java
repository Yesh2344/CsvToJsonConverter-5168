package com.example.csvtojson.validator;

/**
 * Thrown when a CSV row does not satisfy validation rules.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}