package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.MatcherFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

class MealRestControllerTest extends AbstractControllerTest {
    private static final String REST_MEALS_URL = MealRestController.REST_MEALS_URL + "/";

    @Autowired
    MealService mealService;

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_MEALS_URL + meal1.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(meal1.id(), SecurityUtil.authUserId()));
    }

    @Test
    void createWithUri() throws Exception {
        Meal newMeal = getNew();
        ResultActions resultActions =
                perform(MockMvcRequestBuilders.post(REST_MEALS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(newMeal)))
                        .andDo(print())
                        .andExpect(status().isCreated());
        Meal created = MEAL_MATCHER.readFromJson(resultActions);
        int newId = created.id();
        newMeal.setId(newId);
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(mealService.get(newId, SecurityUtil.authUserId()), newMeal);
    }

    @Test
    void update() throws Exception {
        Meal updMeal = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_MEALS_URL + meal1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updMeal)))
                .andDo(print())
                .andExpect(status().isOk());
        MEAL_MATCHER.assertMatch(mealService.get(updMeal.id(), SecurityUtil.authUserId()),
                updMeal);
    }

    @Test
    void getBetween() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_MEALS_URL + "filter?")
                .params(dateTimeRange))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MatcherFactory.usingIgnoringFieldsComparator(MealTo.class)
                        .contentJson(MealsUtil.getTos(mealService.getBetweenInclusive(
                                        LocalDate.of(2020, Month.JANUARY, 30),
                                        LocalDate.of(2020, Month.JANUARY, 30), USER_ID),
                                SecurityUtil.authUserCaloriesPerDay())));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_MEALS_URL + meal1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal1));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_MEALS_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MatcherFactory.usingIgnoringFieldsComparator(MealTo.class)
                        .contentJson(MealsUtil.getTos(meals, SecurityUtil.authUserCaloriesPerDay())));
    }
}