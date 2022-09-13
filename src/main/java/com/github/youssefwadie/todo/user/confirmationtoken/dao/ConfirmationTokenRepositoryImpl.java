package com.github.youssefwadie.todo.user.confirmationtoken.dao;

import com.github.youssefwadie.todo.model.ConfirmationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ConfirmationTokenRepositoryImpl implements ConfirmationTokenRepository {
    public static final String INSERT_CONFIRMATION_TOKEN_TEMPLATE = """
            INSERT INTO confirmation_tokens (token, created_at, expired_at, user_id)
            VALUES (?, ?, ?, ?)
            """;
    public static final String QUERY_FIND_BY_ID_TEMPLATE = """
            SELECT * FROM confirmation_tokens WHERE id = ?
            """;

    public static final String QUERY_FIND_BY_TOKEN_TEMPLATE = """
            SELECT * FROM confirmation_tokens WHERE token = ?
            """;

    public static final String QUERY_COUNT_ALL = "SELECT COUNT(*) FROM confirmation_tokens";
    public static final String DELETE_BY_ID_TEMPLATE = "DELETE FROM confirmation_tokens WHERE id = ?";
    private static final String DELETE_ALL_BY_USER_ID_TEMPLATE = "DELETE FROM confirmation_tokens WHERE user_id = ?";
    private static final String DELETE_ALL = "DELETE FROM confirmation_tokens";
    private static final String UPDATE_SET_CONFIRMATION_STATUS_BY_ID_TEMPLATE = "UPDATE confirmation_tokens SET confirmed = ? WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ConfirmationToken> rowMapper = new ConfirmationTokenRowMapper();

    @Override
    @Transactional
    @Modifying
    public ConfirmationToken save(ConfirmationToken confirmationToken) {
        Assert.notNull(confirmationToken, "confirmationToken must not be null");
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_CONFIRMATION_TOKEN_TEMPLATE, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, confirmationToken.getToken());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(confirmationToken.getCreatedAt()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(confirmationToken.getExpiredAt()));
            preparedStatement.setLong(4, confirmationToken.getUserId());
            return preparedStatement;
        }, keyHolder);
        Number generatedKey = keyHolder.getKey();
        if (generatedKey == null) throw new IncorrectResultSetColumnCountException(1, 0);
        Long tokenId = generatedKey.longValue();
        return findById(tokenId).orElseThrow(() -> new IncorrectResultSetColumnCountException(1, 0));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConfirmationToken> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        ConfirmationToken confirmationToken = jdbcTemplate.queryForObject(QUERY_FIND_BY_ID_TEMPLATE, rowMapper, id);
        return (confirmationToken == null) ? Optional.empty() : Optional.of(confirmationToken);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConfirmationToken> findByToken(String token) {
        Assert.notNull(token, "token must not be null");
        ConfirmationToken confirmationToken = jdbcTemplate.queryForObject(QUERY_FIND_BY_TOKEN_TEMPLATE, rowMapper, token);
        return (confirmationToken == null) ? Optional.empty() : Optional.of(confirmationToken);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        Long confirmationTokensCount = jdbcTemplate.queryForObject(QUERY_COUNT_ALL, Long.class);
        return confirmationTokensCount == null ? 0 : confirmationTokensCount;
    }

    @Override
    @Transactional
    @Modifying
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_TEMPLATE, id);
    }

    @Override
    @Transactional
    @Modifying
    public void deleteAllByUserId(Long userId) {
        jdbcTemplate.update(DELETE_ALL_BY_USER_ID_TEMPLATE, userId);
    }

    @Override
    @Transactional
    @Modifying
    public void deleteAll() {
        jdbcTemplate.update(DELETE_ALL);
    }

    @Override
    @Transactional
    @Modifying
    public void setConfirmedStatus(Long id, boolean status) {
        jdbcTemplate.update(UPDATE_SET_CONFIRMATION_STATUS_BY_ID_TEMPLATE, status, id);
    }
}
