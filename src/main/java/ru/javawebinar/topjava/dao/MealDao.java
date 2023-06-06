package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.util.List;

public interface MealDao {
    List<MealTo> getAllMealTos();

    boolean delete(Integer id);

    Integer update(Meal meal);

    Integer add(Meal meal);

    Meal getById(Integer id);

    default void loadTestValues() {
    }
}
