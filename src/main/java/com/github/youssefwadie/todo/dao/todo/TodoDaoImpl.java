package com.github.youssefwadie.todo.dao.todo;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.util.Streamable;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.github.youssefwadie.todo.model.Todo;

@Repository
public class TodoDaoImpl implements TodoDao {
    public static final String INSERT_TODO_TEMPLATE = "INSERT INTO todos" +
            " (title, description, dead_time, user_id)" +
            " VALUES (?, ?, ?, ?)";


    public static final String QUERY_FIND_ALL = "SELECT * FROM todos";
    public static final String QUERY_FIND_BY_ID_TEMPLATE = "SELECT * FROM todos WHERE id = ?";
    public static final String QUERY_FIND_BY_USER_ID_TEMPLATE = "SELECT * FROM todos WHERE user_id = ?";

    public static final String QUERY_CHECK_IF_EXISTS_BY_ID_TEMPLATE = "SELECT COUNT(*) > 0 FROM todos WHERE id = ?";
    public static final String QUERY_CHECK_IF_BELONGS_TO_USER_BY_ID_AND_USER_ID_TEMPLATE = "SELECT COUNT(*) > 0 FROM todos WHERE id = ? AND user_id = ?";


    public static final String QUERY_COUNT_ALL = "SELECT COUNT(*) FROM todos";
    public static final String QUERY_COUNT_ALL_BY_USER_ID_TEMPLATE = "SELECT COUNT(*) FROM todos WHERE user_id = ?";


    public static final String DELETE_ALL = "DELETE FROM todos";
    public static final String DELETE_BY_ID_TEMPLATE = "DELETE FROM todos WHERE id = ?";
    public static final String DELETE_ALL_BY_USER_ID_TEMPLATE = "DELETE FROM todos WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final TodoRowMapper rowMapper;

    public TodoDaoImpl(JdbcTemplate jdbcTemplate, TodoRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }


    @Override
    public Todo save(Todo todo) {
        Assert.notNull(todo, "Todo must not be null!");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_TODO_TEMPLATE, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, todo.getTitle());
            preparedStatement.setString(2, todo.getDescription());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(todo.getDeadTime()));
            preparedStatement.setLong(4, todo.getUserId());
            return preparedStatement;
        }, keyHolder);
        Long updatedTodoId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : todo.getId();

        return findById(updatedTodoId).orElseThrow(() -> new IncorrectResultSetColumnCountException(1, 0));
    }

    @Override
    public Iterable<Todo> saveAll(Iterable<Todo> todos) {
        Assert.notNull(todos, "Todos must not be null!");
        return Streamable.of(todos)
                .stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Todo> findById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        Todo todo = jdbcTemplate.queryForObject(QUERY_FIND_BY_ID_TEMPLATE, rowMapper, id);
        if (todo == null) {
            return Optional.empty();
        }
        return Optional.of(todo);
    }

    @Override
    public boolean existsById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        Boolean exists = jdbcTemplate.queryForObject(QUERY_CHECK_IF_EXISTS_BY_ID_TEMPLATE, Boolean.class, id);
        return exists != null ? exists : false;
    }

    @Override
    public Iterable<Todo> findAll() {
        return jdbcTemplate.query(QUERY_FIND_ALL, rowMapper);
    }

    @Override
    public Iterable<Todo> findAllById(Iterable<? extends Long> ids) {
        List<Todo> todos = new ArrayList<>();
        ids.forEach(id -> findById(id).ifPresent(todos::add));
        return todos;
    }

    public long count() {
        Long todosCount = jdbcTemplate.queryForObject(QUERY_COUNT_ALL, Long.class);
        return todosCount == null ? 0 : todosCount;
    }

    public void deleteById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        jdbcTemplate.update(DELETE_BY_ID_TEMPLATE, id);
    }

    public void deleteAll(Iterable<Todo> todos) {
        Assert.notNull(todos, "Todos must not be null!");
        todos.forEach(this::delete);
    }

    @Override
    public void delete(Todo todo) {
        Assert.notNull(todo, "Todo must not be null!");
        deleteById(todo.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update(DELETE_ALL);
    }

    @Override
    public Iterable<Todo> findAllByUserId(Long userId) {
        Assert.notNull(userId, "User id must not be null!");
        return jdbcTemplate.query(QUERY_FIND_BY_USER_ID_TEMPLATE, rowMapper, userId);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        Assert.notNull(userId, "User id must not be null!");
        jdbcTemplate.update(DELETE_ALL_BY_USER_ID_TEMPLATE, userId);
    }

    @Override
    public long countByUserId(Long userId) {
        Assert.notNull(userId, "User id must not be null!");
        Long count = jdbcTemplate.queryForObject(QUERY_COUNT_ALL_BY_USER_ID_TEMPLATE, Long.class, userId);
        return count != null ? count : 0;
    }

    @Override
    public boolean belongsToUser(Long id, Long userId) {
        Assert.notNull(id, "Id must not be null!");
        Assert.notNull(userId, "User id must not be null!");
        Boolean belongs = jdbcTemplate.queryForObject(QUERY_CHECK_IF_BELONGS_TO_USER_BY_ID_AND_USER_ID_TEMPLATE, Boolean.class, id, userId);
        return belongs != null ? belongs : false;
    }
}
