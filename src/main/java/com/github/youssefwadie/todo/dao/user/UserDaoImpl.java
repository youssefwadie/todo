package com.github.youssefwadie.todo.dao.user;

import com.github.youssefwadie.todo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    public static final String QUERY_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    public static final String INSERT_USER = """
            INSERT INTO users(email, password)
            VALUES (?, ?)
            """;
    public static final String QUERY_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    public static final String QUERY_USER_EXISTS_BY_EMAIL = "SELECT COUNT(id) > 0 FROM users WHERE email = ?";
    public static final String QUERY_USER_EXISTS_BY_ID = "SELECT COUNT(id) > 0 FROM users WHERE id = ?";


    private final JdbcTemplate jdbcTemplate;


    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = jdbcTemplate.queryForObject(QUERY_USER_BY_EMAIL, new UserRowMapper(), email);
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(QUERY_USER_EXISTS_BY_ID, Boolean.class, id));
    }

    @Override
    public boolean existsByEmail(String userEmail) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(QUERY_USER_EXISTS_BY_EMAIL, Boolean.class, userEmail));
    }

    @Override
    public User save(User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            return preparedStatement;
        }, keyHolder);
        Long updatedUserId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : user.getId();
        return jdbcTemplate.queryForObject(QUERY_USER_BY_ID, new UserRowMapper(), updatedUserId);
    }
}
