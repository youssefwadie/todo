package com.github.youssefwadie.todo.todoitem.dao;

import com.github.youssefwadie.todo.model.TodoItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TodoItemRowMapper implements RowMapper<TodoItem> {
    @Override
    public TodoItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        LocalDateTime deadline = rs.getTimestamp("deadline").toLocalDateTime();
        boolean done = rs.getBoolean("done");
        Long userId = rs.getLong("user_id");

        return new TodoItem(id, title, description, deadline, done, userId);
    }
}
