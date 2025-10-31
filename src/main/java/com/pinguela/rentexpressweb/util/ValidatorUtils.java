package com.pinguela.rentexpressweb.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class ValidatorUtils {

    private ValidatorUtils() {
        // Utility class
    }

    public static String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static Integer parseInteger(String rawValue) {
        String sanitized = sanitize(rawValue);
        if (sanitized == null) {
            return null;
        }
        try {
            return Integer.valueOf(sanitized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static Integer parseInteger(String rawValue, List<String> errors, String errorMessage) {
        Integer parsed = parseInteger(rawValue);
        if (rawValue != null && parsed == null) {
            addError(errors, errorMessage);
        }
        return parsed;
    }

    public static BigDecimal parsePositiveBigDecimal(String rawValue, List<String> errors, String invalidMessage,
            String negativeMessage) {
        String sanitized = sanitize(rawValue);
        if (sanitized == null) {
            return null;
        }
        try {
            BigDecimal value = new BigDecimal(sanitized);
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                addError(errors, negativeMessage);
                return null;
            }
            return value;
        } catch (NumberFormatException ex) {
            addError(errors, invalidMessage);
            return null;
        }
    }

    public static Integer parseYear(String rawValue, int minYear, int maxYear, List<String> errors,
            String invalidMessage, String rangeMessage) {
        String sanitized = sanitize(rawValue);
        if (sanitized == null) {
            return null;
        }
        try {
            int year = Integer.parseInt(sanitized);
            if (year < minYear || year > maxYear) {
                addError(errors, rangeMessage);
                return null;
            }
            return Integer.valueOf(year);
        } catch (NumberFormatException ex) {
            addError(errors, invalidMessage);
            return null;
        }
    }

    public static int parsePositiveInt(String rawValue, int defaultValue, List<String> errors, String errorMessage) {
        String sanitized = sanitize(rawValue);
        if (sanitized == null) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(sanitized);
            if (value < 1) {
                addError(errors, errorMessage);
                return defaultValue;
            }
            return value;
        } catch (NumberFormatException ex) {
            addError(errors, errorMessage);
            return defaultValue;
        }
    }

    public static int parseAllowedInteger(String rawValue, int defaultValue, Collection<Integer> allowedValues,
            List<String> errors, String errorMessage) {
        String sanitized = sanitize(rawValue);
        if (sanitized == null) {
            return defaultValue;
        }
        try {
            Integer value = Integer.valueOf(sanitized);
            if (allowedValues == null || allowedValues.contains(value)) {
                return value.intValue();
            }
            addError(errors, errorMessage);
            return defaultValue;
        } catch (NumberFormatException ex) {
            addError(errors, errorMessage);
            return defaultValue;
        }
    }

    public static boolean parseBooleanFlag(String rawValue) {
        String sanitized = sanitize(rawValue);
        if (sanitized == null) {
            return false;
        }
        String normalized = sanitized.toLowerCase(Locale.ROOT);
        return "true".equals(normalized) || "on".equals(normalized) || "1".equals(normalized)
                || "yes".equals(normalized) || "si".equals(normalized) || "sí".equals(normalized);
    }

    public static Date requireDate(String rawValue, String errorMessage, List<String> errors) {
        String sanitized = sanitize(rawValue);
        if (sanitized == null) {
            addError(errors, errorMessage);
            return null;
        }
        Date parsed = LegacyDateUtils.parseIsoDate(sanitized);
        if (parsed == null) {
            addError(errors, errorMessage);
        }
        return parsed;
    }

    private static void addError(List<String> errors, String message) {
        if (errors != null && message != null && !message.trim().isEmpty()) {
            errors.add(message);
        }
    }
}
