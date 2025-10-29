package com.pinguela.rentexpressweb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class LegacyDateUtilsTest {

    @Test
    public void parseIsoDateShouldReturnNullWhenValueIsNull() {
        assertNull(LegacyDateUtils.parseIsoDate(null));
    }

    @Test
    public void parseIsoDateShouldReturnNullWhenValueIsEmpty() {
        assertNull(LegacyDateUtils.parseIsoDate("   "));
    }

    @Test
    public void parseIsoDateShouldReturnNullWhenValueHasInvalidFormat() {
        assertNull(LegacyDateUtils.parseIsoDate("2023/08/16"));
    }

    @Test
    public void parseIsoDateShouldParseValidIsoDate() {
        Date result = LegacyDateUtils.parseIsoDate("2023-08-16");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(result);
        assertEquals(2023, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(16, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void daysBetweenShouldReturnZeroWhenStartIsNull() {
        Date end = new Date();
        assertEquals(0, LegacyDateUtils.daysBetween(null, end));
    }

    @Test
    public void daysBetweenShouldReturnZeroWhenEndIsNull() {
        Date start = new Date();
        assertEquals(0, LegacyDateUtils.daysBetween(start, null));
    }

    @Test
    public void daysBetweenShouldReturnZeroWhenDifferenceIsNonPositive() {
        Date start = new Date(1_000L);
        Date end = new Date(1_000L);
        assertEquals(0, LegacyDateUtils.daysBetween(start, end));
        assertEquals(0, LegacyDateUtils.daysBetween(end, start));
    }

    @Test
    public void daysBetweenShouldReturnWholeDaysBetweenDates() {
        Date start = new Date(0L);
        Date end = new Date(3L * 24L * 60L * 60L * 1000L);
        assertEquals(3, LegacyDateUtils.daysBetween(start, end));
    }

    @Test
    public void daysBetweenShouldCapAtIntegerMaxValue() {
        Date start = new Date(0L);
        long overflowMillis = ((long) Integer.MAX_VALUE + 10L) * 24L * 60L * 60L * 1000L;
        Date end = new Date(start.getTime() + overflowMillis);
        assertEquals(Integer.MAX_VALUE, LegacyDateUtils.daysBetween(start, end));
    }

    @Test
    public void toTimestampShouldReturnNullWhenValueIsNull() {
        assertNull(LegacyDateUtils.toTimestamp(null));
    }

    @Test
    public void toTimestampShouldReturnSameInstanceWhenValueIsTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        assertSame(timestamp, LegacyDateUtils.toTimestamp(timestamp));
    }

    @Test
    public void toTimestampShouldConvertDate() {
        Date now = new Date();
        Timestamp timestamp = LegacyDateUtils.toTimestamp(now);
        assertEquals(now.getTime(), timestamp.getTime());
    }

    @Test
    public void toTimestampShouldConvertNumber() {
        long millis = 1_234_567L;
        Timestamp timestamp = LegacyDateUtils.toTimestamp(Long.valueOf(millis));
        assertEquals(millis, timestamp.getTime());
    }

    @Test
    public void toTimestampShouldConvertIsoString() {
        Timestamp timestamp = LegacyDateUtils.toTimestamp("2023-08-16T10:15:30");
        assertEquals(Timestamp.valueOf("2023-08-16 10:15:30"), timestamp);
    }

    @Test
    public void toTimestampShouldReturnNullWhenStringIsInvalid() {
        assertNull(LegacyDateUtils.toTimestamp("invalid-timestamp"));
    }

    @Test
    public void toTimestampShouldReturnNullWhenStringIsEmpty() {
        assertNull(LegacyDateUtils.toTimestamp("   "));
    }

    @Test
    public void toDateShouldReturnNullWhenValueIsNull() {
        assertNull(LegacyDateUtils.toDate(null));
    }

    @Test
    public void toDateShouldConvertTimestamp() {
        Timestamp timestamp = Timestamp.valueOf("2023-08-16 10:15:30");
        Date date = LegacyDateUtils.toDate(timestamp);
        assertEquals(timestamp.getTime(), date.getTime());
    }

    @Test
    public void toDateShouldConvertString() {
        Date date = LegacyDateUtils.toDate("2023-08-16 10:15:30");
        assertEquals(Timestamp.valueOf("2023-08-16 10:15:30").getTime(), date.getTime());
    }
}
