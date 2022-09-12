package com.github.youssefwadie.todo.user.role;

import com.github.youssefwadie.todo.model.Role;
import org.springframework.data.util.Streamable;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    public static final String INSERT_ROLE_TEMPLATE = "INSERT INTO roles (name, description) VALUES (?, ?)";
    private static final String UPDATE_ROLE_BY_ID_TEMPLATE = "UPDATE roles SET name = ?, description = ? WHERE id = ?";

    private static final String INSERT_USER_ROLE_TEMPLATE = "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)";

    public static final String QUERY_FIND_BY_ID_TEMPLATE = "SELECT * FROM roles WHERE id = ?";
    public static final String QUERY_FIND_BY_NAME_TEMPLATE = "SELECT * FROM roles WHERE name = ?";
    public static final String QUERY_FIND_ALL = "SELECT * FROM roles";

    public static final String QUERY_COUNT_ALL = "SELECT COUNT(*) FROM roles";

    public static final String DELETE_BY_ID_TEMPLATE = "DELETE FROM roles WHERE id = ?";

    public static final String DELETE_ALL = "DELETE FROM roles";
    private static final String QUERY_FIND_ALL_BY_USER_ID =
            "SELECT _r_.id, _r_.name, _r_.description FROM roles AS _r_ JOIN users_roles AS _ur_ ON _r_.id = _ur_.role_id WHERE _ur_.user_id = ?";
    private static final String QUERY_CHECK_IF_EXISTS_BY_ID_TEMPLATE = "SELECT COUNT(*) > 0 FROM roles WHERE id = ?";
    private static final String QUERY_CHECK_IF_USER_HAS_ROLE_TEMPLATE
            = "SELECT COUNT(*) > 0 FROM users_roles WHERE user_id = ? AND role_id = ?";
    private static final String DELETE_USER_ROLE_TEMPLATE = "DELETE FROM users_roles WHERE user_id = ? AND role_id = ?";
    private static final String DELETE_ALL_USERS_ROLES_BY_ID_TEMPLATE = "DELETE FROM users_roles WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Role> rowMapper;

    public RoleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new RoleRowMapper();
    }

    @Override
    public Role save(Role role) {
        Assert.notNull(role, "Role must not be null!");
        if (role.getId() != null) {
            jdbcTemplate.update(UPDATE_ROLE_BY_ID_TEMPLATE,
                    role.getName(),
                    role.getDescription(),
                    role.getId());
            return role;
        }


        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_ROLE_TEMPLATE, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, role.getName());
            preparedStatement.setString(2, role.getDescription());
            return preparedStatement;
        }, keyHolder);

        Long updatedTodoId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : role.getId();

        return findById(updatedTodoId).orElseThrow(() -> new IncorrectResultSetColumnCountException(1, 0));
    }

    @Override
    public Iterable<Role> saveAll(Iterable<Role> roles) {
        Assert.notNull(roles, "todoItems must not be null!");
        return Streamable.of(roles)
                .stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Role> findById(Long id) {
        Assert.notNull(id, "Id must not be null!");

        Role role = jdbcTemplate.queryForObject(QUERY_FIND_BY_ID_TEMPLATE, rowMapper, id);
        if (role == null) return Optional.empty();
        return Optional.of(role);
    }

    @Override
    public Iterable<Role> findAll() {
        return jdbcTemplate.query(QUERY_FIND_ALL, rowMapper);
    }

    @Override
    public Iterable<Role> findAllById(Iterable<Long> ids) {
        Assert.notNull(ids, "IDs must not be null");
        return Streamable.of(ids)
                .stream()
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        Long rolesCount = jdbcTemplate.queryForObject(QUERY_COUNT_ALL, Long.class);
        return rolesCount == null ? 0 : rolesCount;
    }

    @Override
    public void deleteById(Long id) {
        Assert.notNull(id, "Id must not be null");
        jdbcTemplate.update(DELETE_BY_ID_TEMPLATE, id);
    }

    @Override
    public void delete(Role role) {
        Assert.notNull(role, "Role must not be null");
        deleteById(role.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        Assert.notNull(ids, "IDs must not be null");
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Role> roles) {
        Assert.notNull(roles, "Roles must not be null");
        roles.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update(DELETE_ALL);
    }

    @Override
    public List<Role> findAllByUserId(Long id) {
        Assert.notNull(id, "Id must not be null");
        return jdbcTemplate.query(QUERY_FIND_ALL_BY_USER_ID, rowMapper, id);
    }


    private void deleteUserRoleById(Long roleId, Long userId) {
        jdbcTemplate.update(DELETE_USER_ROLE_TEMPLATE, userId, roleId);
    }

    @Override
    public void deleteUsersRolesById(List<Long> rolesIds, Long userId) {
        Assert.notNull(rolesIds, "Roles IDs must not be null");
        Assert.notNull(userId, "User Id must not be null");
        for (Long role : rolesIds) {
            deleteUserRoleById(role, userId);
        }
    }

    @Override
    public void deleteAllUsersRolesById(Long userId) {
        Assert.notNull(userId, "userId must not be null");
        jdbcTemplate.update(DELETE_ALL_USERS_ROLES_BY_ID_TEMPLATE, userId);
    }


    @Override
    public void saveAllForUser(List<Role> roles, Long userId) {
        Assert.notNull(roles, "roles must not be null");
        Assert.notNull(userId, "userId must not be null");

        List<Role> userRolesInDB = findAllByUserId(userId);
        if (roles.isEmpty()) return;
        if (userRolesInDB.equals(roles)) return;

        List<Long> deletedRoles = userRolesInDB
                .stream()
                .filter(role -> !roles.contains(role))
                .map(Role::getId)
                .toList();
        deleteUsersRolesById(deletedRoles, userId);

        for (Role role : roles) {
            Assert.notNull(role, "roles must not contain any null value");
            if (role.getId() == null) {
                role = save(role);
            }

            boolean userHasRole = Boolean.TRUE.equals(
                    jdbcTemplate.queryForObject(QUERY_CHECK_IF_USER_HAS_ROLE_TEMPLATE, Boolean.class, userId, role.getId())
            );
            if (!userHasRole) {
                jdbcTemplate.update(INSERT_USER_ROLE_TEMPLATE, userId, role.getId());
            }

        }
    }

    @Override
    public boolean existsById(Long id) {
        Assert.notNull(id, "Id must not be null!");
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(QUERY_CHECK_IF_EXISTS_BY_ID_TEMPLATE, Boolean.class, id));
    }

    @Override
    public Optional<Role> findByName(String name) {
        Assert.notNull(name, "Name must not be null!");
        Role role = jdbcTemplate.queryForObject(QUERY_FIND_BY_NAME_TEMPLATE, rowMapper, name);
        return role == null ? Optional.empty() : Optional.of(role);
    }
}
