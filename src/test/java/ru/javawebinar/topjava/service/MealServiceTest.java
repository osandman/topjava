package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void findedMealIsMatch() {
        Meal meal = service.get(userMeal1.getId(), USER_ID);
        assertMatch(meal, userMeal1);
    }

    @Test
    public void notFoundMealByAnotherUser() {
        assertThrows(NotFoundException.class, () -> service.get(userMeal1.getId(), ADMIN_ID));
    }

    @Test
    public void notFoundToDelete() {
        assertThrows(NotFoundException.class, () -> service.delete(adminMeal1.getId(), USER_ID));
    }

    @Test
    public void deleteWithMatchExceptions() {
        assertThatNoException().isThrownBy(() -> service.delete(adminMeal1.getId(), ADMIN_ID));
        assertThrows(NotFoundException.class, () -> service.get(adminMeal1.getId(), ADMIN_ID));
    }

    @Test
    public void findedBetweenInclusiveIsMatch() {
        List<Meal> betweenInclusiveMeal = service.getBetweenInclusive(LocalDate.of(2023, Month.JUNE, 29),
                LocalDate.of(2023, Month.JUNE, 29), USER_ID);
        assertMatch(betweenInclusiveMeal, sortByDateTimeReversed(userMealsOf2023Jun29));
    }

    @Test
    public void findedAllUserMealIsMatch() {
        List<Meal> meals = service.getAll(USER_ID);
        assertMatch(meals, sortByDateTimeReversed(userMeals));
    }

    @Test
    public void updatedMealIsMatch() {
        Meal updatetMeal = getUpdatedMeal();
        service.update(updatetMeal, USER_ID);
        assertMatch(service.get(getUpdatedMeal().getId(), USER_ID), getUpdatedMeal());
    }

    @Test
    public void notFoundToUpdate() {
        Meal updatetMeal = getUpdatedMeal();
        assertThrows(NotFoundException.class, () -> service.update(updatetMeal, ADMIN_ID));
    }

    @Test
    public void updatedMealIsDuplicate() {
        Meal updatetMeal = getUpdatedDuplicatedMeal();
        assertThrows(DataAccessException.class, () -> service.update(updatetMeal, USER_ID));
    }

    @Test
    public void createdMealIsMatch() {
        Meal newMeal = service.create(getNewMeal(), USER_ID);
        Integer newId = newMeal.getId();
        Meal expected = getNewMeal();
        expected.setId(newId);
        assertMatch(newMeal, expected);
    }

    @Test
    public void createdMealIsDuplicate() {
        assertThrows(DataAccessException.class, () -> service.create(duplicatedUserMeal1, USER_ID));
    }
}