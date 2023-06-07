package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryMealDao implements MealDao {
    private final AtomicInteger idCounter = new AtomicInteger();
    private final Map<Integer, Meal> storage;

    public MemoryMealDao() {
        storage = new ConcurrentHashMap<>();
        loadTestValues();
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean delete(int id) {
        return storage.remove(id) != null;
    }

    @Override
    public Meal update(Meal meal) {
        return storage.computeIfPresent(meal.getId(), (id, prevMeal) -> meal);
    }

    @Override
    public Meal add(Meal meal) {
        return storage.computeIfAbsent(idCounter.incrementAndGet(), id -> {
            meal.setId(id);
            return meal;
        });
    }

    @Override
    public Meal getById(int id) {
        return storage.get(id);
    }

    private void loadTestValues() {
        MealsUtil.getTestMeals().forEach(this::add);
    }
}
