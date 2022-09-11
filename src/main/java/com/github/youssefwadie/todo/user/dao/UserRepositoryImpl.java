package com.github.youssefwadie.todo.user.dao;

import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.util.BasicValidator;
import com.github.youssefwadie.todo.todoitem.dao.TodoItemRepository;
import com.github.youssefwadie.todo.user.role.RoleRepository;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    public static final String INSERT_NEW_USER_TEMPLATE = "INSERT INTO users (email, password, created_at, updated_at) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_USER_TEMPLATE = "UPDATE users SET email = ?, password = ?, updated_at = ?, enabled = ? WHERE id = ?";

    public static final String QUERY_FIND_BY_ID_TEMPLATE = "SELECT * FROM users WHERE id = ?";
    public static final String QUERY_FIND_BY_EMAIL_TEMPLATE = "SELECT * FROM users WHERE email = ?";
    public static final String QUERY_CHECK_IF_EXISTS_BY_EMAIL_TEMPLATE = "SELECT COUNT(id) > 0 FROM users WHERE email = ?";
    public static final String QUERY_CHECK_IF_EXISTS_BY_ID_TEMPLATE = "SELECT COUNT(id) > 0 FROM users WHERE id = ?";
    public static final String QUERY_COUNT_ALL = "SELECT COUNT(*) FROM users";

    public static final String QUERY_FIND_ALL = "SELECT * FROM users";

    public static final String DELETE_BY_ID_TEMPLATE = "DELETE FROM users WHERE id = ?";
    public static final String DELETE_BY_EMAIL_TEMPLATE = "DELETE FROM users WHERE email = ?";

    public static final String DELETE_ALL = "DELETE FROM users";
    private static final String UPDATE_USER_STATUS_BY_ID_TEMPLATE = "UPDATE users SET enabled = ? WHERE id = ?";

    private final RoleRepository roleRepository;
    private final TodoItemRepository todoItemRepository;

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, RoleRepository roleRepository, TodoItemRepository todoItemRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleRepository = roleRepository;
        this.todoItemRepository = todoItemRepository;
        this.rowMapper = new UserRowMapper();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Assert.notNull(email, "Email must not be null!");
        User user = jdbcTemplate.queryForObject(QUERY_FIND_BY_EMAIL_TEMPLATE, rowMapper, email);
        if (user == null) return Optional.empty();

        user.setRoles(roleRepository.findAllByUserId(user.getId()));
        return Optional.of(user);
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
        User user = jdbcTemplate.queryForObject(QUERY_FIND_BY_ID_TEMPLATE, rowMapper, id);
        if (user == null) {
            return Optional.empty();
        }

        user.setRoles(roleRepository.findAllByUserId(id));
        return Optional.of(user);
    }

    @Override
    public boolean existsById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(QUERY_CHECK_IF_EXISTS_BY_ID_TEMPLATE, Boolean.class, id));
    }

    @Override
    public Iterable<User> findAll() {
        return jdbcTemplate.query(QUERY_FIND_ALL, rowMapper);
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> ids) {
        Assert.notNull(ids, "IDs must not be null");
        List<User> users = new LinkedList<>();
        ids.forEach(id -> findById(id).ifPresent(users::add));
        return users;
    }

    @Override
    public long count() {
        Long usersCount = jdbcTemplate.queryForObject(QUERY_COUNT_ALL, Long.class);
        return usersCount == null ? 0 : usersCount;
    }

    @Override
    public void deleteById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        roleRepository.deleteAllUsersRolesById(id);
        todoItemRepository.deleteAllByUserId(id);
        jdbcTemplate.update(DELETE_BY_ID_TEMPLATE, id);
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

        throw new IllegalArgumentException("Either user's email or id must not be null");
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
        jdbcTemplate.update(DELETE_ALL);
    }

    @Override
    public boolean existsByEmail(String email) {
        Assert.notNull(email, "Email must not be null!");
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(QUERY_CHECK_IF_EXISTS_BY_EMAIL_TEMPLATE, Boolean.class, email));
    }

    // TODO: optimize it a bit.
    @Override
    public void deleteByEmail(String email) {
        Assert.notNull(email, "Email must not be null!");
        Optional<User> optionalUser = findByEmail(email);
        optionalUser.ifPresent(user -> deleteById(user.getId()));
//        jdbcTemplate.update(DELETE_BY_EMAIL_TEMPLATE, email);
    }

    @Override
    @Transactional
    public User save(User user) {
        Assert.notNull(user, "The saved user must not be null!");

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        if (user.getId() != null && existsById(user.getId())) {
            jdbcTemplate.update(con -> {
                PreparedStatement preparedStatement = con.prepareStatement(UPDATE_USER_TEMPLATE, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setBoolean(4, user.isEnabled());
                preparedStatement.setLong(5, user.getId());
                return preparedStatement;
            });
        } else {
            jdbcTemplate.update(con -> {
                PreparedStatement preparedStatement = con.prepareStatement(INSERT_NEW_USER_TEMPLATE, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setTimestamp(4, null);
                return preparedStatement;
            }, keyHolder);

        }
        Long updatedUserId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : user.getId();
        roleRepository.saveAllForUser(user.getRoles(), updatedUserId);
        return findById(updatedUserId).orElseThrow(() -> new IncorrectResultSetColumnCountException(1, 0));
    }

    @Override
    public void updateUserStatus(Long id, boolean status) {
        jdbcTemplate.update(UPDATE_USER_STATUS_BY_ID_TEMPLATE, status, id);
    }
}
