package com.github.youssefwadie.todo.dao.user;

import com.github.youssefwadie.todo.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String email = rs.getString("email");
        String password = rs.getString("password");

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        LocalDateTime createdAtTime = createdAt.toLocalDateTime();
        LocalDateTime updatedAtTime = updatedAt != null ? updatedAt.toLocalDateTime() : null;

        return new User(id, email, password, createdAtTime, updatedAtTime);
    }
}
