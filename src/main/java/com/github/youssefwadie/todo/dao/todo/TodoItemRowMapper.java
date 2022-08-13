package com.github.youssefwadie.todo.dao.todo;

import com.github.youssefwadie.todo.model.TodoItem;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class TodoItemRowMapper implements RowMapper<TodoItem> {
    @Override
    public TodoItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        LocalDateTime deadTime = rs.getTimestamp("dead_time").toLocalDateTime();
        Boolean done = rs.getBoolean("done");
        Long userId = rs.getLong("user_id");

        return new TodoItem(id, title, description, deadTime, done, userId);
    }
}
