package com.github.youssefwadie.todo.user.confirmationtoken.dao;

import com.github.youssefwadie.todo.model.ConfirmationToken;
import com.github.youssefwadie.todo.model.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ConfirmationTokenRepository {
    /**
     * Saves a confirmation token.
     *
     * @param confirmationToken must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal confirmationToken} is {@literal null}.
     */
    @Transactional
    @Modifying
    void save(ConfirmationToken confirmationToken);

    /**
     * Retrieves a {@link ConfirmationToken confirmationToken} by id.
     *
     * @param id must not be {@literal null}.
     * @return the confirmation token with the given id or {@literal  Optional#empty()} if non found.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    Optional<ConfirmationToken> findById(Long id);


    /**
     * Returns the number of confirmation tokens available.
     *
     * @return the number of confirmation tokens.
     */
    @Transactional(readOnly = true)
    long count();

    /**
     * Deletes the confirmation token with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Transactional
    @Modifying
    void deleteById(Long id);

    /**
     * Deletes all {@link ConfirmationToken confirmationToken} with the given user ID.
     *
     * @param userId must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal userId} is {@literal null}.
     */
    @Transactional
    @Modifying
    void deleteAllByUserId(Long userId);


    /**
     * Deletes all the confirmation tokens.
     */
    @Transactional
    @Modifying
    void deleteAll();

    /**
     * Updates the token's status (make it confirmed).
     * @param id token id must not be {@literal null}.
     * @param status new confirmation status
     * @throws IllegalArgumentException in case the given {@literal id}
     */
    @Transactional
    @Modifying
    void setConfirmedStatus(Long id, boolean status);
}
