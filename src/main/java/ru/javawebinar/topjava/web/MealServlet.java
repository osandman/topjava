package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealDaoMemoryImpl;
import ru.javawebinar.topjava.model.Meal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String SHOW_MEALS_JSP = "/meals.jsp";
    private static final String EDIT_MEALS_JSP = "/edit_meal.jsp";
    private MealDao mealDao;

    @Override
    public void init() throws ServletException {
        mealDao = MealDaoMemoryImpl.getInstance();
        mealDao.loadTestValues();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("enter in doGet");
        setEncoding(req, resp);
        String forwardUrl = "";
        req.setAttribute("dtFormatter", getDateTimeFormatter());
        String action = req.getParameter("action");
        int mealId;
        switch (action != null ? action : "show") {
            case "delete":
                mealId = Integer.parseInt(req.getParameter("id"));
                boolean isDelete = mealDao.delete(mealId);
                log.debug(String.format("delete meal: id=%d, success=%s", mealId, isDelete));
                forwardUrl = SHOW_MEALS_JSP;
                req.setAttribute("mealsTo", mealDao.getAllMealTos());
                break;
            case "edit":
                mealId = Integer.parseInt(req.getParameter("id"));
                forwardUrl = EDIT_MEALS_JSP;
                Meal editMeal = mealDao.getById(mealId);
                req.setAttribute("meal", editMeal);
                break;
            case "add":
                forwardUrl = EDIT_MEALS_JSP;
                req.setAttribute("meal", null);
                break;
            case "show":
                forwardUrl = SHOW_MEALS_JSP;
                req.setAttribute("mealsTo", mealDao.getAllMealTos());
                break;
            default:
                break;
        }
        log.debug("redirect to " + forwardUrl);
        req.getRequestDispatcher(forwardUrl).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("enter in doPost");
        setEncoding(req, resp);
        try {
            String description = req.getParameter("description");
            LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("dateTime"));
            int calories = Integer.parseInt(req.getParameter("calories"));
            String mealId = req.getParameter("id");
            if (mealId != null && !mealId.isEmpty()) {
                int editMealId = mealDao.update(new Meal(Integer.parseInt(mealId), dateTime, description, calories));
                log.debug("update meal id=" + editMealId);
            } else {
                int newMealId = mealDao.add(new Meal(dateTime, description, calories));
                log.debug("add meal id=" + newMealId);
            }
            doGet(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Input correct values");
            req.getRequestDispatcher(EDIT_MEALS_JSP).forward(req, resp);
        }
    }

    private static void setEncoding(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html; charset=UTF-8");
    }

    private static DateTimeFormatter getDateTimeFormatter() {
        final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";
        final Locale LOCALE = Locale.ROOT;
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, LOCALE);
    }
}
