package com.github.youssefwadie.todo.user.confirmationtoken.dao;

import com.github.youssefwadie.todo.model.ConfirmationToken;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ConfirmationTokenRepository {
    /**
     * Saves a confirmation token.
     *
     * @param confirmationToken must not be {@literal null}.
     * Use the returned instance for further operations as the save operation might have changed the ConfirmationToken instance completely.
     * @return the saved confirmationToken; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal confirmationToken} is {@literal null}.
     */
    ConfirmationToken save(ConfirmationToken confirmationToken);

    /**
     * Retrieves a {@link ConfirmationToken confirmationToken} by id.
     *
     * @param id must not be {@literal null}.
     * @return the confirmation token with the given id or {@literal  Optional#empty()} if non found.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}.
     */
    Optional<ConfirmationToken> findById(Long id);

    /**
     * Retrieves a {@link ConfirmationToken confirmationToken} by id.
     *
     * @param token must not be {@literal null}.
     * @return the confirmation token with the given token or {@literal  Optional#empty()} if non found.
     * @throws IllegalArgumentException in case the given {@literal token} is {@literal null}.
     */
    Optional<ConfirmationToken> findByToken(String token);

    /**
     * Returns the number of confirmation tokens available.
     *
     * @return the number of confirmation tokens.
     */
    long count();

    /**
     * Deletes the confirmation token with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    void deleteById(Long id);

    /**
     * Deletes all {@link ConfirmationToken confirmationToken} with the given user ID.
     *
     * @param userId must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal userId} is {@literal null}.
     */
    void deleteAllByUserId(Long userId);


    /**
     * Deletes all the confirmation tokens.
     */
    void deleteAll();

    /**
     * Updates the token's status (make it confirmed).
     * @param id token id must not be {@literal null}.
     * @param status new confirmation status
     * @throws IllegalArgumentException in case the given {@literal id}
     */
    void setConfirmedStatus(Long id, boolean status);
}
