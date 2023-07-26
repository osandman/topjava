package ru.javawebinar.topjava.web.meal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.formatter.TopJavaDateTimeFormat;
import ru.javawebinar.topjava.util.formatter.TopJavaDateTimeFormat.Type;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value = MealRestController.REST_MEALS_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController extends AbstractMealController {
    static final String REST_MEALS_URL = "/rest/meals";

    @Override
    @GetMapping("/{id}")
    public Meal get(@PathVariable int id) {
        return super.get(id);
    }

    @Override
    @GetMapping
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Meal> createWithUri(@RequestBody Meal meal) {
        Meal newMeal = super.create(meal);
        URI newMealUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_MEALS_URL + "/{id}")
                .buildAndExpand(newMeal.getId()).toUri();
        return ResponseEntity.created(newMealUri).body(newMeal);
    }

    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Meal meal, @PathVariable int id) {
        super.update(meal, id);
    }

    @GetMapping("/filter")
    public List<MealTo> getBetween(@RequestParam(required = false)
                                   @TopJavaDateTimeFormat(type = Type.DATE)
                                   LocalDate startDate,
                                   @RequestParam(required = false)
                                   @TopJavaDateTimeFormat(type = Type.TIME)
                                   LocalTime startTime,
                                   @RequestParam(required = false)
                                   @TopJavaDateTimeFormat(type = Type.DATE)
                                   LocalDate endDate,
                                   @RequestParam(required = false)
                                   @TopJavaDateTimeFormat(type = Type.TIME)
                                   LocalTime endTime) {
        return super.getBetween(startDate, startTime, endDate, endTime);
    }
}