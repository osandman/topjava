package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        System.out.println("filteredByCycles");
        filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
        filteredByCycles(meals, LocalTime.of(0, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);

        System.out.println("filteredByStreams");
        filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
        filteredByStreams(meals, LocalTime.of(0, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);

        System.out.println("filteredByOneCycle");
        filteredByReflection(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
        filteredByReflection(meals, LocalTime.of(0, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
    }

    // Return filtered list with excess. Implement by 2 cycles
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = new HashMap<>();
        for (UserMeal userMeal : meals) {
            caloriesByDays.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> userMealWithExcessList = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalDate currentDay = userMeal.getDateTime().toLocalDate();
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                userMealWithExcessList.add(new UserMealWithExcess(userMeal.getDateTime(),
                        userMeal.getDescription(), userMeal.getCalories(), caloriesByDays.get(currentDay) > caloriesPerDay));
            }
        }
        return userMealWithExcessList;
    }

    // Implement by streams
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = meals.stream()
                .collect(Collectors.toMap(meal -> meal.getDateTime().toLocalDate(), UserMeal::getCalories, Integer::sum));
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesByDays.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    // Implement by reflection API
    public static List<UserMealWithExcess> filteredByReflection(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = new HashMap<>();
        Map<LocalDate, List<UserMealWithExcess>> excessDays = new HashMap<>();
        List<UserMealWithExcess> userMealWithExcessList = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalDate currentDay = userMeal.getDateTime().toLocalDate();
            caloriesByDays.merge(currentDay, userMeal.getCalories(), Integer::sum);
            excessDays.putIfAbsent(currentDay, new ArrayList<>());
            boolean excess = caloriesByDays.get(currentDay) > caloriesPerDay;
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                UserMealWithExcess currentMealWithExcess = new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(),
                        userMeal.getCalories(), excess);
                userMealWithExcessList.add(currentMealWithExcess);
                excessDays.get(currentDay).add(currentMealWithExcess);
            }
            List<UserMealWithExcess> toChange = excessDays.get(currentDay);
            if (toChange != null) {
                for (UserMealWithExcess userMealWithExcess : toChange) {
                    try {
                        Field excessField = UserMealWithExcess.class.getDeclaredField("excess");
                        excessField.setAccessible(true);
                        excessField.setBoolean(userMealWithExcess, excess);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return userMealWithExcessList;
    }
}
