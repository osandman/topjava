package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MemoryMealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String SHOW_MEALS_JSP = "/meals.jsp";
    private static final String EDIT_MEALS_JSP = "/edit_meal.jsp";
    private static final String SHOW_MEALS_SERVLET = "/meals";
    private final static DateTimeFormatter DATE_TIME_FORMATTER = getDateTimeFormatter();
    private MealDao mealDao;

    @Override
    public void init() throws ServletException {
        mealDao = new MemoryMealDao();
        loadTestValues();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("enter in doGet");
        String forwardUrl = "";
        String action = req.getParameter("action");
        int mealId;
        switch (action != null ? action : "default") {
            case "delete":
                mealId = Integer.parseInt(req.getParameter("id"));
                boolean isDelete = mealDao.delete(mealId);
                log.debug("delete meal: id={}, success={}", mealId, isDelete);
                resp.sendRedirect(req.getContextPath() + SHOW_MEALS_SERVLET);
                log.debug("redirect to {}", SHOW_MEALS_SERVLET);
                return;
            case "edit":
                mealId = Integer.parseInt(req.getParameter("id"));
                forwardUrl = EDIT_MEALS_JSP;
                Meal editMeal = mealDao.getById(mealId);
                req.setAttribute("meal", editMeal);
                break;
            case "add":
                forwardUrl = EDIT_MEALS_JSP;
                break;
            default:
                forwardUrl = SHOW_MEALS_JSP;
                setAttributesForMealsShow(req);
                break;
        }
        req.getRequestDispatcher(forwardUrl).forward(req, resp);
        log.debug("forward to {}", forwardUrl);
    }

    private void setAttributesForMealsShow(HttpServletRequest req) {
        final int caloriesExcess = 2000;
        req.setAttribute("dtFormatter", DATE_TIME_FORMATTER);
        req.setAttribute("mealsTo", MealsUtil.filteredByStreams(mealDao.getAllMeals(), caloriesExcess));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("enter in doPost");
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String description = req.getParameter("description");
        LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("dateTime"));
        int calories = Integer.parseInt(req.getParameter("calories"));
        String mealId = req.getParameter("id");
        if (mealId != null && !mealId.isEmpty()) {
            Meal editMeal = mealDao.update(new Meal(Integer.parseInt(mealId), dateTime, description, calories));
            log.debug("update meal: {}", editMeal.toString());
        } else {
            Meal newMeal = mealDao.add(new Meal(dateTime, description, calories));
            log.debug("add meal: {}", newMeal);
        }
        resp.sendRedirect(req.getContextPath() + SHOW_MEALS_SERVLET);
        log.debug("redirect to {}", SHOW_MEALS_SERVLET);
    }

    public void loadTestValues() {
        MealsUtil.getTestMeals().forEach(meal -> mealDao.add(meal));
    }

    private static DateTimeFormatter getDateTimeFormatter() {
        final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    }
}
