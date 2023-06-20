package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {
    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final SimpleJdbcInsert insertMeal;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        insertMeal = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("userId", userId)
                .addValue("description", meal.getDescription())
                .addValue("dateTime", meal.getDateTime())
                .addValue("calories", meal.getCalories());
        if (meal.isNew()) {
            Number id = insertMeal.executeAndReturnKey(paramMap);
            meal.setId(id.intValue());
        } else if (namedJdbcTemplate.update(
                "UPDATE meals SET description=:description, date_time=:dateTime," +
                " calories=:calories WHERE id=:id AND user_id=:userId", paramMap) == 0) {
            return null;
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
        List<Meal> query = jdbcTemplate.query(sql, ROW_MAPPER, id, userId);
        return DataAccessUtils.singleResult(query);
    }

    @Override
    public List<Meal> getAll(int userId) {
        String sql = "SELECT * FROM meals WHERE user_id=? ORDER BY date_time DESC";
        return jdbcTemplate.query(sql, ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        String sql = "SELECT * FROM meals WHERE user_id=? AND date_time>=? AND date_time<? " +
                     "ORDER BY date_time DESC";
        return jdbcTemplate.query(sql, ROW_MAPPER, userId, startDateTime, endDateTime);
    }
}
