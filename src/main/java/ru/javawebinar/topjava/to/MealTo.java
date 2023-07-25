package ru.javawebinar.topjava.to;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;

@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.ANY)
public record MealTo(Integer id,
                     LocalDateTime dateTime,
                     String description,
                     int calories,
                     boolean excess) {
}
