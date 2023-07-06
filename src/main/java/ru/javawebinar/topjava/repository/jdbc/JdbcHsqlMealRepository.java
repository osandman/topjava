package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Profile("hsqldb")
@Repository
public class JdbcHsqlMealRepository extends AbstractJdbcMealRepository {
    public JdbcHsqlMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    <T extends Comparable<?>> T convertDateTime(LocalDateTime dateTime) {
        return (T) Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
