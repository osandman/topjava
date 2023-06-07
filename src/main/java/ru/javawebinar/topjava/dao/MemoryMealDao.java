package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

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
        idCounter.set(0);
    }

    @Override
    public List<Meal> getAllMeals() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean delete(int id) {
        return storage.remove(id) != null;
    }

    @Override
    public Meal update(Meal meal) {
        storage.put(meal.getId(), meal);
        return storage.get(meal.getId());
    }

    @Override
    public Meal add(Meal meal) {
        meal.setId(idCounter.incrementAndGet());
        storage.put(idCounter.get(), meal);
        return storage.get(idCounter.get());
    }

    @Override
    public Meal getById(int id) {
        return storage.get(id);
    }
}
