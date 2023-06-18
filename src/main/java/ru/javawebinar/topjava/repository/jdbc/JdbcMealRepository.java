package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.Util;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class JdbcMealRepository implements MealRepository {
    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);
    JdbcTemplate jdbcTemplate;
    SimpleJdbcInsert insertMeal;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate) {
        insertMeal = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                    .addValue("id", meal.getId())
                    .addValue("user_id", userId)
                    .addValue("description", meal.getDescription())
                    .addValue("dateTime", meal.getDateTime())
                    .addValue("calories", meal.getCalories());
            Number id = insertMeal.executeAndReturnKey(parameterSource);
            meal.setId(id.intValue());
        } else {
            if (jdbcTemplate.update("UPDATE meals SET description=?, date_time=?, calories=? WHERE id=? AND user_id=?",
                    meal.getDescription(), meal.getDateTime(), meal.getCalories(), meal.getId(), userId) == 0) {
                return null;
            }
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        String sql = "DELETE FROM meals WHERE id=? AND user_id=?";
        return jdbcTemplate.update(sql, id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        String sql = "SELECT * FROM meals WHERE id=? AND user_id=?";
        return jdbcTemplate.queryForObject(sql, ROW_MAPPER, id, userId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filterByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return filterByPredicate(userId, meal -> Util.isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime));
    }

    private List<Meal> filterByPredicate(int userId, Predicate<Meal> filter) {
        String sql = "SELECT * FROM meals WHERE user_id=?";
        List<Meal> meals = jdbcTemplate.query(sql, ROW_MAPPER, userId);
        return meals.stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}
