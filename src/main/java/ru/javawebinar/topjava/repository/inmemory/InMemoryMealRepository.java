package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            int id = counter.incrementAndGet();
            meal.setIds(id, userId);
            repository.put(id, meal);
            return meal;
        }
        return repository.computeIfPresent(meal.getId(), (id, prevMeal) -> {
            if (prevMeal.getUserId() == userId) {
                meal.setUserId(userId);
                return meal;
            }
            return null;
        });
    }

    @Override
    public boolean delete(int id, int userId) {
        return repository.computeIfPresent(id, (mealId, meal) -> {
            if (meal.getUserId() == userId) {
                return meal;
            }
            return null;
        }) != null && repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        return (repository.containsKey(id) && repository.get(id).getUserId() == userId) ?
                repository.get(id) : null;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.values().stream()
                .filter(meal -> meal.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getFiltered(int userId, LocalDateTime start, LocalDateTime end) {
        return getAll(userId).stream()
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDateTime(), start, end))
                .collect(Collectors.toList());
    }
}

