package com.pinguela.rentexpressweb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;

public class LegacyDateUtilsTest {

    @Test
    public void toDateShouldReturnNullWhenValueIsNull() {
        assertNull(LegacyDateUtils.toDate(null));
    }

    @Test
    public void toDateShouldCopyDateInstances() {
        Date original = new Date();
        Date copy = LegacyDateUtils.toDate(original);
        assertNotSame(original, copy);
        assertEquals(original.getTime(), copy.getTime());
    }

    @Test
    public void toDateShouldConvertTimestamp() {
        Timestamp timestamp = Timestamp.valueOf("2023-08-16 10:15:30");
        Date converted = LegacyDateUtils.toDate(timestamp);
        assertEquals(timestamp.getTime(), converted.getTime());
    }

    @Test
    public void toDateShouldConvertNumber() {
        long millis = 1_234_567L;
        Date converted = LegacyDateUtils.toDate(Long.valueOf(millis));
        assertEquals(millis, converted.getTime());
    }

    @Test
    public void toDateShouldParseTimestampStrings() {
        Date converted = LegacyDateUtils.toDate("2023-08-16T10:15:30");
        assertEquals(Timestamp.valueOf("2023-08-16 10:15:30").getTime(), converted.getTime());
    }

    @Test
    public void toDateShouldParseTimestampStringsWithExtraSpaces() {
        Date converted = LegacyDateUtils.toDate(" 2023-08-16T10:15:30 ");
        assertEquals(Timestamp.valueOf("2023-08-16 10:15:30").getTime(), converted.getTime());
    }

    @Test
    public void toDateShouldReturnNullWhenStringIsInvalid() {
        assertNull(LegacyDateUtils.toDate("invalid"));
    }

    @Test
    public void toDateShouldReturnNullWhenStringIsEmpty() {
        assertNull(LegacyDateUtils.toDate("   "));
    }

    @Test
    public void formatDateShouldReturnEmptyStringWhenDateOrPatternIsNull() {
        assertEquals("", LegacyDateUtils.formatDate(null, "yyyy-MM-dd"));
        assertEquals("", LegacyDateUtils.formatDate(new Date(), null));
    }

    @Test
    public void formatDateShouldFormatUsingPattern() {
        Date date = Timestamp.valueOf("2023-08-16 10:15:30");
        String formatted = LegacyDateUtils.formatDate(date, "yyyy-MM-dd");
        assertEquals("2023-08-16", formatted);
    }
}
