package com.pinguela.rentexpressweb.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilidades para trabajar con fechas usando las APIs clásicas de Java.
 */
public final class LegacyDateUtils {

    private LegacyDateUtils() {
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null || pattern == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ROOT);
        return format.format(date);
    }

    public static Date toDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return new Date(((Date) value).getTime());
        }
        if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }

        String text = value.toString();
        if (text == null) {
            return null;
        }
        String normalized = text.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        String timestampCandidate = normalized.indexOf('T') >= 0 ? normalized.replace('T', ' ') : normalized;
        try {
            return new Date(java.sql.Timestamp.valueOf(timestampCandidate).getTime());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
