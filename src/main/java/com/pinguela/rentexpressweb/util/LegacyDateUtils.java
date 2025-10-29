package com.pinguela.rentexpressweb.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utilidades para trabajar con fechas usando las APIs clásicas de Java.
 */
public final class LegacyDateUtils {

    private static final String ISO_DATE_PATTERN = "yyyy-MM-dd";

    private LegacyDateUtils() {
    }

    public static Date parseIsoDate(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(ISO_DATE_PATTERN, Locale.ROOT);
        format.setLenient(false);
        try {
            return format.parse(trimmed);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null || pattern == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ROOT);
        return format.format(date);
    }

    public static int daysBetween(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        long diff = end.getTime() - start.getTime();
        if (diff <= 0L) {
            return 0;
        }
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        if (days > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) days;
    }

    public static Timestamp toTimestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        if (value instanceof Date) {
            return new Timestamp(((Date) value).getTime());
        }
        if (value instanceof Number) {
            return new Timestamp(((Number) value).longValue());
        }
        String text = value.toString();
        if (text == null) {
            return null;
        }
        String normalized = text.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.indexOf('T') >= 0) {
            normalized = normalized.replace('T', ' ');
        }
        try {
            return Timestamp.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static Date toDate(Object value) {
        Timestamp timestamp = toTimestamp(value);
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp.getTime());
    }
}
