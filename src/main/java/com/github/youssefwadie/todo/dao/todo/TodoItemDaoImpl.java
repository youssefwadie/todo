package com.github.youssefwadie.todo.dao.todo;

import com.github.youssefwadie.todo.model.TodoItem;
import org.springframework.data.util.Streamable;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TodoItemDaoImpl implements TodoItemDao {
    public static final String INSERT_TODO_ITEM_TEMPLATE = "INSERT INTO todo_items" +
            " (title, description, dead_time, user_id)" +
            " VALUES (?, ?, ?, ?)";

    public static final String UPDATE_TODO_ITEM_BY_ID_TEMPLATE = "UPDATE todo_items " +
            "SET title = ?, description = ?, dead_time = ?, done = ? " +
            "WHERE id = ?";

    public static final String UPDATE_TODO_ITEM_SET_DONE_BY_ID_TEMPLATE = "UPDATE todo_items " +
            "SET done = ? " +
            "WHERE id = ?";
    public static final String QUERY_FIND_ALL = "SELECT * FROM todo_items";
    public static final String QUERY_FIND_BY_ID_TEMPLATE = "SELECT * FROM todo_items WHERE id = ?";
    public static final String QUERY_FIND_BY_USER_ID_TEMPLATE = "SELECT * FROM todo_items WHERE user_id = ?";

    public static final String QUERY_CHECK_IF_EXISTS_BY_ID_TEMPLATE = "SELECT COUNT(*) > 0 FROM todo_items WHERE id = ?";
    public static final String QUERY_CHECK_IF_BELONGS_TO_USER_BY_ID_AND_USER_ID_TEMPLATE = "SELECT COUNT(*) > 0 FROM todo_items WHERE id = ? AND user_id = ?";


    public static final String QUERY_COUNT_ALL = "SELECT COUNT(*) FROM todo_items";
    public static final String QUERY_COUNT_ALL_BY_USER_ID_TEMPLATE = "SELECT COUNT(*) FROM todo_items WHERE user_id = ?";


    public static final String DELETE_ALL = "DELETE FROM todo_items";
    public static final String DELETE_BY_ID_TEMPLATE = "DELETE FROM todo_items WHERE id = ?";
    public static final String DELETE_ALL_BY_USER_ID_TEMPLATE = "DELETE FROM todo_items WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final TodoItemRowMapper rowMapper;

    public TodoItemDaoImpl(JdbcTemplate jdbcTemplate, TodoItemRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }


    @Override
    public TodoItem save(TodoItem todo) {
        Assert.notNull(todo, "Todo must not be null!");
        if (todo.getId() != null) {
            jdbcTemplate.update(UPDATE_TODO_ITEM_BY_ID_TEMPLATE,
                    todo.getTitle(),
                    todo.getDescription(),
                    todo.getDeadTime(),
                    todo.getDone(),
                    todo.getId());

            return todo;
        }


        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_TODO_ITEM_TEMPLATE, Statement.RETURN_GENERATED_KEYS);
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
    public Iterable<TodoItem> saveAll(Iterable<TodoItem> todoItems) {
        Assert.notNull(todoItems, "todoItems must not be null!");
        return Streamable.of(todoItems)
                .stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TodoItem> findById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        TodoItem todo = jdbcTemplate.queryForObject(QUERY_FIND_BY_ID_TEMPLATE, rowMapper, id);
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
    public Iterable<TodoItem> findAll() {
        return jdbcTemplate.query(QUERY_FIND_ALL, rowMapper);
    }

    @Override
    public Iterable<TodoItem> findAllById(Iterable<? extends Long> ids) {
        List<TodoItem> todoItems = new ArrayList<>();
        ids.forEach(id -> findById(id).ifPresent(todoItems::add));
        return todoItems;
    }

    public long count() {
        Long itemsCount = jdbcTemplate.queryForObject(QUERY_COUNT_ALL, Long.class);
        return itemsCount == null ? 0 : itemsCount;
    }

    public void deleteById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        jdbcTemplate.update(DELETE_BY_ID_TEMPLATE, id);
    }

    public void deleteAll(Iterable<TodoItem> todoItems) {
        Assert.notNull(todoItems, "todoItems must not be null!");
        todoItems.forEach(this::delete);
    }

    @Override
    public void delete(TodoItem todo) {
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
    public Iterable<TodoItem> findAllByUserId(Long userId) {
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
    public boolean ownedByUser(Long id, Long userId) {
        Assert.notNull(id, "Id must not be null!");
        Assert.notNull(userId, "User id must not be null!");
        Boolean belongs = jdbcTemplate.queryForObject(QUERY_CHECK_IF_BELONGS_TO_USER_BY_ID_AND_USER_ID_TEMPLATE, Boolean.class, id, userId);
        return belongs != null ? belongs : false;
    }

    @Override
    public void setDone(Long id, boolean done) {
        jdbcTemplate.update(UPDATE_TODO_ITEM_SET_DONE_BY_ID_TEMPLATE,
                done,
                id);
    }
}
