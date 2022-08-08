package com.github.youssefwadie.todo.dao.todo;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.youssefwadie.todo.model.Todo;

@Repository
public interface TodoDao {

    /**
     * Saves a given todo. Use the returned instance for further operations as the save operation might have changed the
     * user instance completely.
     *
     * @param todo must not be {@literal null}.
     * @return the saved todo; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal todo} is {@literal null}.
     */

    @Transactional
    @Modifying
    Todo save(Todo todo);

    /**
     * Saves all given todos.
     *
     * @param todos must not be {@literal null} nor must it contain {@literal null}.
     * @return the saved todos; will never be {@literal null}. The returned {@literal Iterable} will have the same size
     * as the {@literal Iterable} passed as an argument.
     * @throws IllegalArgumentException in case the given {@link Iterable todos} or one of its todos is {@literal null}.
     */
    @Transactional
    Iterable<Todo> saveAll(Iterable<Todo> todos);

    /**
     * Retrieves a {@link Todo todo} by its id.
     *
     * @param id must not be {@literal null}.
     * @return the user with the given email or {@literal  Optional#empty()} if non found.
     * @throws IllegalArgumentException in case the given {@literal user} is {@literal null}.
     */
    @Transactional(readOnly = true)
    Optional<Todo> findById(Long id);

    /**
     * Returns whether todo with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return {@literal true} if a todo with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    boolean existsById(Long id);

    /**
     * Returns all todos.
     *
     * @return all todos
     */
    @Transactional(readOnly = true)
    Iterable<Todo> findAll();


    /**
     * Returns all {@link Todo todos} with the given IDs.
     * <p>
     * If some or all ids are not found, no todos are returned for these IDs.
     * <p>
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
     * {@literal ids}.
     * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
     */
    @Transactional(readOnly = true)
    Iterable<Todo> findAllById(Iterable<? extends Long> ids);

    /**
     * Returns the number of todos available.
     *
     * @return the number of todos.
     */
    @Transactional(readOnly = true)
    long count();

    /**
     * Deletes the todo with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Transactional
    @Modifying
    void deleteById(Long id);


    /**
     * Deletes the given todo.
     *
     * @param todo must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null} or {@literal not null} but {@literal the todo's} id {@literal null}
     */
    @Transactional
    @Modifying
    void delete(Todo todo);

    /**
     * Deletes all {@link Todo todos} with the given IDs.
     *
     * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal ids} or one of its todos is {@literal null}.
     */
    @Transactional
    @Modifying
    void deleteAllById(Iterable<? extends Long> ids);


    /**
     * Deletes the given users.
     *
     * @param todos must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal todos} or one of the todos is {@literal null}.
     */
    @Transactional
    @Modifying
    void deleteAll(Iterable<Todo> todos);

    /**
     * Deletes all todos.
     */
    @Transactional
    @Modifying
    void deleteAll();

    /**
     * finds all the todos for given userId
     *
     * @param userId must not be {@literal null}.
     * @return all the todos for given user by their id
     * @throws IllegalArgumentException in case the given id is null
     */
    @Transactional(readOnly = true)
    Iterable<Todo> findAllByUserId(Long userId);

    /**
     * deletes all the todos' for given userId
     *
     * @param userId must not be {@literal null}
     * @throws IllegalArgumentException if the userId is {@literal null}
     */
    @Modifying
    @Transactional
    void deleteAllByUserId(Long userId);

    /**
     * count all the todos created by a user
     *
     * @param userId must not be {@literal null}
     * @return the number of todos for a given user
     * @throws IllegalArgumentException if userId is {@literal null}
     */
    @Transactional(readOnly = true)
    long countByUserId(Long userId);

    /**
     * Returns whether todo with the given id is owned by a user with the given userId.
     *
     * @param id     must not be {@literal null}.
     * @param userId must not be {@literal null}
     * @return {@literal true} if a todo with the given id, belongs to the user with the given id {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null} or {@literal userId} is {@literal null}.
     */
    @Transactional(readOnly = true)
    boolean belongsToUser(Long id, Long userId);
}
