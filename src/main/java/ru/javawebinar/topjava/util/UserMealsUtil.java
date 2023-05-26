package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

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
        filteredByCycles(meals, LocalTime.of(0, 0), LocalTime.of(12, 00), 2000)
                .forEach(System.out::println);
    }

    // Return filtered list with excess. Implement by 2 cycles
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = new HashMap<>();
        for (UserMeal userMeal : meals) {
            LocalDate currentDay = userMeal.getDateTime().toLocalDate();
            caloriesByDays.merge(currentDay, userMeal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> userMealWithExcessList = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalDate currentDay = userMeal.getDateTime().toLocalDate();
            boolean excess = caloriesByDays.get(currentDay) > caloriesPerDay;
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                userMealWithExcessList.add(new UserMealWithExcess(userMeal.getDateTime(),
                        userMeal.getDescription(), userMeal.getCalories(), excess));
            }
        }
        return userMealWithExcessList;
    }

    // Implement by streams
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = meals.stream()
                .collect(Collectors.toMap(el -> el.getDateTime().toLocalDate(), UserMeal::getCalories, Integer::sum));
        return meals.stream()
                .filter(el -> TimeUtil.isBetweenHalfOpen(el.getDateTime().toLocalTime(), startTime, endTime))
                .map(el -> new UserMealWithExcess(el.getDateTime(), el.getDescription(), el.getCalories(),
                        caloriesByDays.get(el.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
