package com.github.youssefwadie.todo.dao.user;

import com.github.youssefwadie.todo.model.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserDao {
    /**
     * Saves a given user. Use the returned instance for further operations as the save operation might have changed the
     * user instance completely.
     *
     * @param user must not be {@literal null}.
     * @return the saved user; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal user} is {@literal null}.
     */
    @Transactional
    @Modifying
    User save(User user);


    /**
     * Saves all given users.
     *
     * @param users must not be {@literal null} nor must it contain {@literal null}.
     * @return the saved users; will never be {@literal null}. The returned {@literal Iterable} will have the same size
     * as the {@literal Iterable} passed as an argument.
     * @throws IllegalArgumentException in case the given {@link Iterable users} or one of its users is
     *                                  {@literal null}.
     */
    @Transactional
    @Modifying
    Iterable<User> saveAll(Iterable<User> users);


    /**
     * Retrieves a {@link User user} by their id.
     *
     * @param id must not be {@literal null}.
     * @return the user with the given id or {@literal  Optional#empty()} if non found.
     * @throws IllegalArgumentException in case the given {@literal user} is {@literal null}.
     */
    @Transactional(readOnly = true)
    Optional<User> findById(Long id);


    /**
     * Returns all users.
     *
     * @return all users
     */
    @Transactional(readOnly = true)
    Iterable<User> findAll();

    /**
     * Returns all {@link User users} with the given IDs.
     * <p>
     * If some or all ids are not found, no users are returned for these IDs.
     * <p>
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
     * {@literal ids}.
     * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
     */

    @Transactional(readOnly = true)
    Iterable<User> findAllById(Iterable<Long> ids);

    /**
     * Returns the number of users available.
     *
     * @return the number of users.
     */
    @Transactional(readOnly = true)
    long count();

    /**
     * Deletes the user with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Transactional
    @Modifying
    void deleteById(Long id);

    /**
     * Deletes the given user.
     *
     * @param user must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal user} is {@literal null} or {@literal not null} but {@literal the user's} email and password are {@literal null}
     */
    @Transactional
    @Modifying
    void delete(User user);

    /**
     * Deletes all {@link User users} with the given IDs.
     *
     * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal ids} or one of its users is {@literal null}.
     */

    @Transactional
    @Modifying
    void deleteAllById(Iterable<? extends Long> ids);


    /**
     * Deletes the given users.
     *
     * @param users must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal users} or one of the users is {@literal null}.
     */
    @Transactional
    @Modifying
    void deleteAll(Iterable<? extends User> users);

    /**
     * Deletes all users.
     */
    @Transactional
    @Modifying
    void deleteAll();


    /**
     * Retrieves a user by their email.
     *
     * @param email must not be {@literal null}.
     * @return the user with the given email or {@link Optional#empty()} if non found.
     * @throws IllegalArgumentException in case the given {@literal user} is {@literal null}.
     */
    @Transactional(readOnly = true)
    Optional<User> findByEmail(String email);


    /**
     * Returns whether user with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return {@literal true} if a user with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    boolean existsById(Long id);


    /**
     * Returns whether user with the given email exists.
     *
     * @param email must not be {@literal null}.
     * @return {@literal true} if a user with the given email exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    boolean existsByEmail(String email);

    /**
     * Deletes the user with the given email.
     *
     * @param email must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal email} is {@literal null}
     */
    @Transactional
    @Modifying
    void deleteByEmail(String email);

}
