package com.github.youssefwadie.todo.dao.todo;

import com.github.youssefwadie.todo.model.Todo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class TodoDaoImpl implements TodoDao {
    private final JdbcTemplate jdbcTemplate;
    private final TodoRowMapper todoRowMapper;

    public static final String QUERY_TODO_BY_USER_ID = "SELECT * FROM todos WHERE user_id = ?";
    public static final String INSERT_TODO = "INSERT INTO todos" +
            " (title, description, dead_time, user_id)" +
            " VALUES (?, ?, ?, ?)";
    public static final String QUERY_TODO_BY_ID = "SELECT * FROM todos WHERE id = ?";

    public TodoDaoImpl(JdbcTemplate jdbcTemplate, TodoRowMapper todoRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.todoRowMapper = todoRowMapper;
    }

    @Override
    public List<Todo> findAllByUserId(Long userId) {
        return jdbcTemplate.query(QUERY_TODO_BY_USER_ID, todoRowMapper, userId);
    }

    @Override
    public Todo save(Todo todo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_TODO, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, todo.getTitle());
            preparedStatement.setString(2, todo.getDescription());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(todo.getDeadTime()));
            preparedStatement.setLong(4, todo.getUserId());
            return preparedStatement;
        }, keyHolder);
        Long updatedTodoId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : todo.getId();

        return jdbcTemplate.queryForObject(QUERY_TODO_BY_ID, todoRowMapper, updatedTodoId);
    }
}
