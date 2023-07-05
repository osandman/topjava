package ru.javawebinar.topjava.repository.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealServiceTest;

import static ru.javawebinar.topjava.MealTestData.ADMIN_MEAL_ID;
import static ru.javawebinar.topjava.UserTestData.*;
@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaMealRepositoryTest extends MealServiceTest {
    @Test
    public void getAllMealByIdAndUserId() {
        User actual = service.getUser(ADMIN_MEAL_ID, ADMIN_ID);
        USER_MATCHER.assertMatch(actual, admin);
    }
}