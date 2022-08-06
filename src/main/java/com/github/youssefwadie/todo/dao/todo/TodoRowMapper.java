package com.github.youssefwadie.todo.dao.todo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.github.youssefwadie.todo.model.Todo;

@Component
public class TodoRowMapper implements RowMapper<Todo> {
    @Override
    public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        LocalDateTime deadTime = rs.getTimestamp("dead_time").toLocalDateTime();
        Boolean done = rs.getBoolean("done");
        Long userId = rs.getLong("user_id");
        
        return new Todo(id, title, description, deadTime, done, userId);
    }
}
