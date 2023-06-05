package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MealDaoMemoryImpl implements MealDao {
    private final static int CALORIES_LIMIT = 2000;
    private List<Meal> storage;

    public MealDaoMemoryImpl() {
        storage = new CopyOnWriteArrayList<>();
    }

    @Override
    public List<MealTo> getAll() {
        storage = MealsUtil.getTestMeals();
        return MealsUtil.filteredByStreams(storage, CALORIES_LIMIT);
    }
}
