package com.example.csvtojson.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralised logger factory. Guarantees a single logger per class and
 * provides convenient shortcut methods.
 */
public final class LoggerUtil {

    private LoggerUtil() {
        // Utility class
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void info(Class<?> clazz, String message, Object... args) {
        Logger logger = getLogger(clazz);
        if (logger.isInfoEnabled()) {
            logger.info(message, args);
        }
    }

    public static void error(Class<?> clazz, String message, Throwable t) {
        Logger logger = getLogger(clazz);
        logger.error(message, t);
    }

    public static void debug(Class<?> clazz, String message, Object... args) {
        Logger logger = getLogger(clazz);
        if (logger.isDebugEnabled()) {
            logger.debug(message, args);
        }
    }
}