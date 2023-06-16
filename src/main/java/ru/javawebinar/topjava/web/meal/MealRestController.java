package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("getAll");
        List<Meal> allMeals = service.getAll(SecurityUtil.authUserId());
        return MealsUtil.getTos(allMeals, SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getFiltered(String startDay, String endDay, String startTime, String endTime) throws DateTimeParseException {
        log.info("getFiltered");
        LocalDateTime start = parseLocalDate(startDay, startTime, true);
        LocalDateTime end = parseLocalDate(endDay, endTime, false);
        List<Meal> mealsFilteredByDate = service.getFilteredByDays(SecurityUtil.authUserId(),
                start.toLocalDate(), end.toLocalDate().plusDays(1));
        return MealsUtil.getFilteredTos(mealsFilteredByDate, SecurityUtil.authUserCaloriesPerDay(),
                start.toLocalTime(), end.toLocalTime());
    }

    private static LocalDateTime parseLocalDate(String date, String time, boolean isStart) throws DateTimeParseException {
        date = date.isEmpty() ? (isStart ? "0001-01-01" : "9999-12-30") : date;
        time = time.isEmpty() ? (isStart ? LocalTime.MIN.toString() : LocalTime.MAX.toString().substring(0, 5)) : time;
        return LocalDateTime.parse(date + " " + time, DateTimeUtil.DATE_TIME_FORMATTER);
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, SecurityUtil.authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, SecurityUtil.authUserId());
    }

    public void update(Meal meal) {
        int userId = SecurityUtil.authUserId();
        log.info("update {} for user ID {}", meal, userId);
//        assureIdConsistent(meal, userId);
        service.update(meal, SecurityUtil.authUserId());
    }
}