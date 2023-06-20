package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {
    public static final int START_MEAL_ID = UserTestData.GUEST_ID + 1;
    public static final Meal userMeal1 = new Meal(START_MEAL_ID, LocalDateTime.of(2023, Month.JUNE, 29, 10, 0), "Завтрак", 500);
    public static final Meal duplicatedUserMeal1 = new Meal(null, LocalDateTime.of(2023, Month.JUNE, 29, 10, 0), "Двойной Завтрак", 500);
    public static final Meal userMeal2 = new Meal(START_MEAL_ID + 1, LocalDateTime.of(2023, Month.JUNE, 29, 13, 0), "Обед", 1000);
    public static final Meal userMeal3 = new Meal(START_MEAL_ID + 2, LocalDateTime.of(2023, Month.JUNE, 29, 20, 0), "Ужин", 500);
    public static final Meal userMeal4 = new Meal(START_MEAL_ID + 3, LocalDateTime.of(2023, Month.JUNE, 30, 0, 0), "Еда на граничное значение", 100);
    public static final Meal userMeal5 = new Meal(START_MEAL_ID + 4, LocalDateTime.of(2023, Month.JUNE, 30, 10, 0), "Завтрак", 1000);
    public static final Meal userMeal6 = new Meal(START_MEAL_ID + 5, LocalDateTime.of(2023, Month.JUNE, 30, 13, 0), "Обед", 500);
    public static final Meal userMeal7 = new Meal(START_MEAL_ID + 6, LocalDateTime.of(2023, Month.JUNE, 30, 20, 0), "Ужин", 410);
    public static final Meal adminMeal1 = new Meal(START_MEAL_ID + 7, LocalDateTime.of(2023, Month.JUNE, 25, 9, 30), "Завтрак Admin", 1000);
    public static final Meal adminMeal2 = new Meal(START_MEAL_ID + 8, LocalDateTime.of(2023, Month.JUNE, 25, 13, 30), "Обед Admin", 1000);
    public static final Meal adminMeal3 = new Meal(START_MEAL_ID + 9, LocalDateTime.of(2023, Month.JUNE, 25, 20, 0), "Ужин Admin", 500);

    public static final List<Meal> userMeals = new ArrayList<>(Arrays.asList(userMeal1, userMeal2, userMeal3,
            userMeal4, userMeal5, userMeal6, userMeal7));
    public static final List<Meal> userMealsOf2023Jun29 = new ArrayList<>(Arrays.asList(userMeal1, userMeal2, userMeal3));

    public static Meal getNewMeal() {
        return new Meal(LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0),
                "Новогодняя еда", 5000);
    }

    public static Meal getUpdatedMeal() {
        Meal updated = new Meal(userMeal2);
        updated.setDateTime(LocalDateTime.of(2023, Month.MAY, 15, 12, 12));
        updated.setDescription("Обед редакция");
        updated.setCalories(555);
        return updated;
    }

    public static Meal getUpdatedDuplicatedMeal() {
        Meal updated = new Meal(userMeal2);
        updated.setDateTime(LocalDateTime.of(2023, Month.JUNE, 29, 20, 0));
        updated.setDescription("Ужин дубль");
        updated.setCalories(555);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(List<Meal> actual, List<Meal> expected) {
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expected.stream()
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList()));
    }
}
