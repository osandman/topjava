package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MealDaoMemoryImpl implements MealDao {
    private final static int CALORIES_LIMIT = 2000;
    private static final AtomicInteger maxId = new AtomicInteger();
    public static MealDaoMemoryImpl INSTANCE = new MealDaoMemoryImpl();
    private final Map<Integer, Meal> storage;

    private MealDaoMemoryImpl() {
        storage = new ConcurrentHashMap<>();
        maxId.set(0);
    }

    @Override
    public void loadTestValues() {
        storage.clear();
        MealsUtil.getTestMeals().forEach(meal -> {
            if (maxId.get() < meal.getId()) {
                maxId.set(meal.getId());
            }
            storage.put(meal.getId(), meal);
        });
    }

    @Override
    public List<MealTo> getAllMealTos() {
        return MealsUtil.filteredByStreams(new CopyOnWriteArrayList<>(storage.values()), CALORIES_LIMIT);
    }

    @Override
    public boolean delete(Integer id) {
        return storage.remove(id) != null;
    }

    @Override
    public Integer update(Meal meal) {
        storage.replace(meal.getId(), meal);
        return meal.getId();
    }

    @Override
    public Integer add(Meal meal) {
        meal.setId(maxId.incrementAndGet());
        storage.put(maxId.get(), meal);
        return maxId.get();
    }

    @Override
    public Meal getById(Integer id) {
        return storage.get(id);
    }

    public static MealDaoMemoryImpl getInstance() {
        return INSTANCE;
    }
}
