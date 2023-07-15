package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

public class AbstractMealController {

    protected static final Logger log = LoggerFactory.getLogger(AbstractMealController.class);

    @Autowired
    protected MealService service;

    protected Meal getFromParent(int id) {
        int userId = getUserId();
        log.info("get meal {} for user {}", id, userId);
        return service.get(id, userId);
    }

    protected void deleteFromParent(int id) {
        int userId = getUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
    }

    protected List<MealTo> getAllFromParent() {
        int userId = getUserId();
        log.info("getAll for user {}", userId);
        return MealsUtil.getTos(service.getAll(userId), getCaloriesPerDay());
    }

    protected Meal createFromParent(Meal meal) {
        int userId = getUserId();
        checkNew(meal);
        log.info("create {} for user {}", meal, userId);
        return service.create(meal, userId);
    }

    protected void updateFromParent(Meal meal, int id) {
        int userId = getUserId();
        assureIdConsistent(meal, id);
        log.info("update {} for user {}", meal, userId);
        service.update(meal, userId);
    }

    /**
     * <ol>Filter separately
     * <li>by date</li>
     * <li>by time for every date</li>
     * </ol>
     */
    public List<MealTo> getBetween(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                                   @Nullable LocalDate endDate, @Nullable LocalTime endTime) {
        int userId = getUserId();
        log.info("getBetween dates({} - {}) time({} - {}) for user {}", startDate, endDate, startTime, endTime, userId);

        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
        return MealsUtil.getFilteredTos(mealsDateFiltered, getCaloriesPerDay(), startTime, endTime);
    }

    private int getUserId() {
        return SecurityUtil.authUserId();
    }

    private int getCaloriesPerDay() {
        return SecurityUtil.authUserCaloriesPerDay();
    }
}
