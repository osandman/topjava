package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.util.List;

public interface MealDao {
    List<Meal> getAllMeals();

    boolean delete(int id);

    Meal update(Meal meal);

    Meal add(Meal meal);

    Meal getById(int id);

    default void loadTestValues() {
    }
}
