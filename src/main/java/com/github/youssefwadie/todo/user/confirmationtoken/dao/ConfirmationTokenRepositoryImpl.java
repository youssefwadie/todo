package com.github.youssefwadie.todo.user.confirmationtoken.dao;

import com.github.youssefwadie.todo.model.ConfirmationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ConfirmationTokenRepositoryImpl implements ConfirmationTokenRepository {
    public static final String INSERT_CONFIRMATION_TOKEN_TEMPLATE = """
            INSERT INTO confirmation_tokens (token, created_at, expires_at, user_id)
            VALUES (?, ?, ?, ?)
            """;
    public static final String QUERY_FIND_BY_ID_TEMPLATE = """
            SELECT * FROM confirmation_tokens WHERE id = ?
            """;

    public static final String QUERY_COUNT_ALL = "SELECT COUNT(*) FROM confirmation_tokens";
    public static final String DELETE_BY_ID_TEMPLATE = "DELETE FROM confirmation_tokens WHERE id = ?";
    private static final String DELETE_ALL_BY_USER_ID_TEMPLATE = "DELETE FROM confirmation_tokens WHERE user_id = ?";
    private static final String DELETE_ALL = "DELETE FROM confirmation_tokens";
    private static final String UPDATE_SET_CONFIRMATION_STATUS_BY_ID_TEMPLATE = "UPDATE confirmation_tokens SET confirmed = ? WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ConfirmationToken> rowMapper = new ConfirmationTokenRowMapper();

    @Override
    public void save(ConfirmationToken confirmationToken) {
        Assert.notNull(confirmationToken, "confirmationToken must not be null");
        jdbcTemplate.update(INSERT_CONFIRMATION_TOKEN_TEMPLATE,
                confirmationToken.getToken(),
                Timestamp.valueOf(confirmationToken.getCreatedAt()),
                Timestamp.valueOf(confirmationToken.getExpiresAt()),
                confirmationToken.getUserId());
    }

    public Optional<ConfirmationToken> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        ConfirmationToken confirmationToken = jdbcTemplate.queryForObject(QUERY_FIND_BY_ID_TEMPLATE, rowMapper, id);
        return (confirmationToken == null) ? Optional.empty() : Optional.of(confirmationToken);
    }

    @Override
    public long count() {
        Long confirmationTokensCount = jdbcTemplate.queryForObject(QUERY_COUNT_ALL, Long.class);
        return confirmationTokensCount == null ? 0 : confirmationTokensCount;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_TEMPLATE, id);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        jdbcTemplate.update(DELETE_ALL_BY_USER_ID_TEMPLATE, userId);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update(DELETE_ALL);
    }

    @Override
    public void setConfirmedStatus(Long id, boolean status) {
        jdbcTemplate.update(UPDATE_SET_CONFIRMATION_STATUS_BY_ID_TEMPLATE, status, id);
    }
}
