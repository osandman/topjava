package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
        return service.getTos(SecurityUtil.authUserId(), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getFiltered(String startDay, String endDay, String startTime, String endTime) throws DateTimeParseException {
        log.info("getFiltered");
        LocalDateTime start = parseLocalDate(startDay, startTime, true);
        LocalDateTime end = parseLocalDate(endDay, endTime, false);
        return service.getFilteredTos(SecurityUtil.authUserId(), SecurityUtil.authUserCaloriesPerDay(), start, end.plusDays(1));
    }

    private static LocalDateTime parseLocalDate(String date, String time, boolean isStart) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        date = date.isEmpty() ? (isStart ? "0001-01-01" : "9999-12-30") : date;
        time = time.isEmpty() ? (isStart ? LocalTime.MIN.toString() : LocalTime.MAX.toString().substring(0, 5)) : time;
        return LocalDateTime.parse(date + " " + time, formatter);
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
        log.info("update {}", meal);
        service.update(meal, SecurityUtil.authUserId());
    }
}