package ru.javawebinar.topjava.util.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.Locale;

import static ru.javawebinar.topjava.util.DateTimeUtil.TIME_FORMATTER;

public class TimeFormatter implements Formatter<LocalTime> {
    @Override
    public LocalTime parse(String time, Locale locale) throws ParseException {
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    @Override
    public String print(LocalTime localTime, Locale locale) {
        return localTime.toString();
    }
}
