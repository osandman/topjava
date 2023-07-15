package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Transactional(readOnly = true)
@Repository
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional
    @Override
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        }
        insertOrUpdateRoles(user.getRoles().stream().toList(), user.id());
        return user;
    }

    private void insertOrUpdateRoles(List<Role> roles, int id) {
        String sql = """
                INSERT INTO user_role (user_id, role)
                VALUES (?, ?)
                ON CONFLICT (user_id, role) DO UPDATE
                SET role=excluded.role
                """;
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, id);
                        ps.setString(2, String.valueOf(roles.get(i)));
                    }

                    @Override
                    public int getBatchSize() {
                        return roles.size();
                    }
                });
    }

    @Transactional
    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            setUserRoles(user);
        }
        return user;
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            setUserRoles(user);
        }
        return user;
    }

    private void setUserRoles(User user) {
        Set<Role> roles = jdbcTemplate.query("SELECT * FROM user_role WHERE user_id=?",
                rs -> {
                    Set<Role> roleSet = new HashSet<>();
                    while (rs.next()) {
                        roleSet.add(Role.valueOf(rs.getString("role")));
                    }
                    return roleSet;
                }, user.getId());
        user.setRoles(roles);
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        if (!users.isEmpty()) {
            Map<Integer, Set<Role>> roles = jdbcTemplate.query("SELECT * FROM user_role",
                    JdbcUserRepository::getAllUserRoles);
            if (roles != null) {
                users.forEach(user -> user.setRoles(roles.get(user.getId())));
            }
        }
        return users;
    }

    private static Map<Integer, Set<Role>> getAllUserRoles(ResultSet rs) throws SQLException {
        Map<Integer, Set<Role>> rolesById = new HashMap<>();
        while (rs.next()) {
            int id = Integer.parseInt(rs.getString("user_id"));
            Role role = Role.valueOf(rs.getString("role"));
            if (rolesById.get(id) == null) {
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                rolesById.put(id, roles);
            } else {
                rolesById.get(id).add(role);
            }
        }
        return rolesById;
    }
}
