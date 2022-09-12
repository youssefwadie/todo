package com.github.youssefwadie.todo.user.dao;

import com.github.youssefwadie.todo.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String email = rs.getString("email");
        String password = rs.getString("password");
        boolean enabled = rs.getBoolean("enabled");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        LocalDateTime createdAtTime = createdAt.toLocalDateTime();
        LocalDateTime updatedAtTime = updatedAt != null ? updatedAt.toLocalDateTime() : null;

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setEnabled(enabled);
        user.setCreatedAt(createdAtTime);
        user.setUpdatedAt(updatedAtTime);

        return user;
    }
}
