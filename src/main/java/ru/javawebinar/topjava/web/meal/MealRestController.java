package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class MealRestController extends AbstractMealController {
    @Override
    Logger getLog() {
        return LoggerFactory.getLogger(getClass());
    }
}