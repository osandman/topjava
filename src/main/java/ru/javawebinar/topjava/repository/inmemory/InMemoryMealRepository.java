package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
        save(new Meal(LocalDateTime.of(2023, Month.JUNE, 28, 20, 0), "Ужин2", 500), 2);
        save(new Meal(LocalDateTime.of(2023, Month.JUNE, 28, 10, 30), "Завтрак2", 1500), 2);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        }
        // TODO оптимизировать
        // апдейт - между repository.get(meal.getId()) и repository.put(meal.getId(), meal),
        // чтобы апдейт прошел корректно, не должно измениться то, что проверяем в условии стр. 41.
        // Это наличие записи и принадлежность ее этому юзеру. Принадлежность поменяться в принципе не может,
        // а в случае, если запись удалится, можно использовать метод ...IfAbsent, в котором атомарно еще раз
        // проверится наличие и вставится новая запись. И тогда можно обойтись без блока synchronized
        synchronized (repository) {
            Meal updMeal = repository.get(meal.getId());
            if (updMeal != null && updMeal.getUserId() == userId) {
                meal.setUserId(userId);
                repository.put(meal.getId(), meal);
                return meal;
            }
            return null;
        }
    }

    @Override
    public boolean delete(int id, int userId) {
        synchronized (repository) {
            Meal meal = repository.get(id);
            return meal != null && meal.getUserId() == userId && repository.remove(id) != null;
        }
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        return (meal != null && meal.getUserId() == userId) ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filterByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getFiltered(int userId, LocalDate start, LocalDate end) {
        return filterByPredicate(userId, meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDate(), start, end));
    }

    private List<Meal> filterByPredicate(int userId, Predicate<Meal> addFilter) {
        Predicate<Meal> userIdFilter = meal -> meal.getUserId() == userId;
        return repository.values().stream()
                .filter(userIdFilter.and(addFilter))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

