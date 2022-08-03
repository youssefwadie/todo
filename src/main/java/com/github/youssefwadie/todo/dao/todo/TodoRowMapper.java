package com.github.youssefwadie.todo.dao.todo;

import com.github.youssefwadie.todo.model.Todo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TodoRowMapper implements RowMapper<Todo> {
    @Override
    public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        Long userId = rs.getLong("user_id");
        LocalDateTime deadTime = rs.getTimestamp("dead_time").toLocalDateTime();
        return new Todo(id, title, description, userId, deadTime);
    }
}
