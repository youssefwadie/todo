package com.github.youssefwadie.todo.dao.role;

import com.github.youssefwadie.todo.model.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Role(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"));
    }
}
