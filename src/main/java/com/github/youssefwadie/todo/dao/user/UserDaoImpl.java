package com.github.youssefwadie.todo.dao.user;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.util.BasicValidator;
import org.springframework.context.annotation.Primary;
import org.springframework.data.util.Streamable;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
public class UserDaoImpl implements UserDao {
    public static final String INSERT_USER_TEMPLATE = "INSERT INTO users (email, password) VALUES (?, ?)";
    public static final String QUERY_FIND_USER_BY_ID_TEMPLATE = "SELECT * FROM users WHERE id = ?";
    public static final String QUERY_FIND_USER_BY_EMAIL_TEMPLATE = "SELECT * FROM users WHERE email = ?";
    public static final String QUERY_CHECK_IF_USER_EXISTS_BY_EMAIL_TEMPLATE = "SELECT COUNT(id) > 0 FROM users WHERE email = ?";
    public static final String QUERY_CHECK_IF_USER_EXISTS_BY_ID_TEMPLATE = "SELECT COUNT(id) > 0 FROM users WHERE id = ?";
    public static final String QUERY_COUNT_ALL_USERS = "SELECT COUNT(*) FROM users";

    public static final String QUERY_FIND_ALL_USERS_TEMPLATE = "SELECT * FROM users";

    public static final String DELETE_USER_BY_ID_TEMPLATE = "DELETE FROM users WHERE id = ?1";
    public static final String DELETE_USER_BY_EMAIL_TEMPLATE = "DELETE FROM users WHERE email = ?1";

    public static final String DELETE_ALL_USERS = "DELETE FROM users";

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper;

    public UserDaoImpl(JdbcTemplate jdbcTemplate, UserRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Assert.notNull(email, "Email must not be null!");
        User user = jdbcTemplate.queryForObject(QUERY_FIND_USER_BY_EMAIL_TEMPLATE, rowMapper, email);
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }
    }


    @Override
    public Iterable<User> saveAll(Iterable<User> users) {
        Assert.notNull(users, "Users must not be null!");
        return Streamable.of(users)
                .stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        User user = jdbcTemplate.queryForObject(QUERY_FIND_USER_BY_ID_TEMPLATE, rowMapper, id);
        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    @Override
    public boolean existsById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(QUERY_CHECK_IF_USER_EXISTS_BY_ID_TEMPLATE, Boolean.class, id));
    }

    @Override
    public Iterable<User> findAll() {
        return jdbcTemplate.query(QUERY_FIND_ALL_USERS_TEMPLATE, rowMapper);
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> ids) {
        List<User> users = new LinkedList<>();
        ids.forEach(id -> findById(id).ifPresent(users::add));
        return users;
    }

    @Override
    public long count() {
        Long usersCount = jdbcTemplate.queryForObject(QUERY_COUNT_ALL_USERS, Long.class);
        return usersCount == null ? 0 : usersCount;
    }

    @Override
    public void deleteById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        // TODO: delete all the todos of this user
        jdbcTemplate.update(DELETE_USER_BY_ID_TEMPLATE, id);
    }

    @Override
    public void delete(User user) {
        Assert.notNull(user, "User must not be null!");
        if (user.getId() != null) {
            deleteById(user.getId());
            return;
        }
        if (!BasicValidator.isBlank(user.getEmail())) {
            deleteByEmail(user.getEmail());
            return;
        }

        throw new IllegalArgumentException("Either user's email or id's must not be null");
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends User> users) {
        users.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update(DELETE_ALL_USERS);
    }

    @Override
    public boolean existsByEmail(String email) {
        Assert.notNull(email, "Email must not be null!");
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(QUERY_CHECK_IF_USER_EXISTS_BY_EMAIL_TEMPLATE, Boolean.class, email));
    }

    @Override
    public void deleteByEmail(String email) {
        Assert.notNull(email, "Email must not be null!");
        jdbcTemplate.update(DELETE_USER_BY_EMAIL_TEMPLATE, email);
    }

    @Override
    @Transactional
    public User save(User user) {
        Assert.notNull(user, "The saved user must not be null!");

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_USER_TEMPLATE, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            return preparedStatement;
        }, keyHolder);
        Long updatedUserId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : user.getId();
        return findById(updatedUserId).orElseThrow(() -> new IncorrectResultSetColumnCountException(1, 0));
    }
}
