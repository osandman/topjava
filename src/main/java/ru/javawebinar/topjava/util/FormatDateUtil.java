package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FormatDateUtil {
    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";
    private static final Locale LOCALE = Locale.ROOT;

    private FormatDateUtil() {
    }

    public static String formatDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, LOCALE);
        return dateTimeFormatter.format(localDateTime);
    }
}
