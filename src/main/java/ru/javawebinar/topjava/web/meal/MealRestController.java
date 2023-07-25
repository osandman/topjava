package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

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
    public void update(@RequestBody Meal meal, @PathVariable int id) {
        super.update(meal, id);
    }

    @GetMapping("/filter")
    public List<MealTo> getBetween(@RequestParam(required = false)
                                   @DateTimeFormat(iso = ISO.DATE_TIME)
                                   LocalDateTime startDate,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = ISO.DATE_TIME)
                                   LocalDateTime startTime,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = ISO.DATE_TIME)
                                   LocalDateTime endDate,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = ISO.DATE_TIME)
                                   LocalDateTime endTime) {
        return super.getBetween(startDate.toLocalDate(), startTime.toLocalTime(),
                endDate.toLocalDate(), endTime.toLocalTime());
    }
}