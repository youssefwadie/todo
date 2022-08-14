package com.github.youssefwadie.todo.dao.todo;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.youssefwadie.todo.model.TodoItem;

@Repository
public interface TodoItemDao {

    /**
     * Saves a given todoItem. Use the returned instance for further operations as the save operation might have changed the
     * user instance completely.
     *
     * @param todoItem must not be {@literal null}.
     * @return the saved todoItem; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal todoItem} is {@literal null}.
     */

    @Transactional
    @Modifying
    TodoItem save(TodoItem todoItem);

    /**
     * Saves all given todoItems.
     *
     * @param todoItems must not be {@literal null} nor must it contain {@literal null}.
     * @return the saved todoItems; will never be {@literal null}. The returned {@literal Iterable} will have the same size
     * as the {@literal Iterable} passed as an argument.
     * @throws IllegalArgumentException in case the given {@link Iterable todoItems} or one of its todoItems is {@literal null}.
     */
    @Transactional
    Iterable<TodoItem> saveAll(Iterable<TodoItem> todoItems);

    /**
     * Retrieves a {@link TodoItem todoItem} by its id.
     *
     * @param id must not be {@literal null}.
     * @return the user with the given email or {@literal  Optional#empty()} if non found.
     * @throws IllegalArgumentException in case the given {@literal user} is {@literal null}.
     */
    @Transactional(readOnly = true)
    Optional<TodoItem> findById(Long id);

    /**
     * Returns whether todoItem with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return {@literal true} if a todoItem with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    boolean existsById(Long id);

    /**
     * Returns all todoItems.
     *
     * @return all todoItems
     */
    @Transactional(readOnly = true)
    Iterable<TodoItem> findAll();


    /**
     * Returns all {@link TodoItem todoItems} with the given IDs.
     * <p>
     * If some or all ids are not found, no todoItems are returned for these IDs.
     * <p>
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
     * {@literal ids}.
     * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
     */
    @Transactional(readOnly = true)
    Iterable<TodoItem> findAllById(Iterable<? extends Long> ids);

    /**
     * Returns the number of todoItems available.
     *
     * @return the number of todoItems.
     */
    @Transactional(readOnly = true)
    long count();

    /**
     * Deletes the todoItem with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Transactional
    @Modifying
    void deleteById(Long id);


    /**
     * Deletes the given todoItem.
     *
     * @param todoItem must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null} or {@literal not null} but {@literal the todoItem's} id {@literal null}
     */
    @Transactional
    @Modifying
    void delete(TodoItem todoItem);

    /**
     * Deletes all {@link TodoItem todoItems} with the given IDs.
     *
     * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal ids} or one of its todoItems is {@literal null}.
     */
    @Transactional
    @Modifying
    void deleteAllById(Iterable<? extends Long> ids);


    /**
     * Deletes the given users.
     *
     * @param todoItems must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal todoItems} or one of the todoItems is {@literal null}.
     */
    @Transactional
    @Modifying
    void deleteAll(Iterable<TodoItem> todoItems);

    /**
     * Deletes all todoItems.
     */
    @Transactional
    @Modifying
    void deleteAll();

    /**
     * finds all the todoItems for given userId
     *
     * @param userId must not be {@literal null}.
     * @return all the todoItems for given user by their id
     * @throws IllegalArgumentException in case the given id is null
     */
    @Transactional(readOnly = true)
    Iterable<TodoItem> findAllByUserId(Long userId);

    /**
     * deletes all the todoItems' for given userId
     *
     * @param userId must not be {@literal null}
     * @throws IllegalArgumentException if the userId is {@literal null}
     */
    @Modifying
    @Transactional
    void deleteAllByUserId(Long userId);

    /**
     * count all the todoItems created by a user
     *
     * @param userId must not be {@literal null}
     * @return the number of todoItems for a given user
     * @throws IllegalArgumentException if userId is {@literal null}
     */
    @Transactional(readOnly = true)
    long countByUserId(Long userId);

    /**
     * Returns whether todoItem with the given id is owned by a user with the given userId.
     *
     * @param id     must not be {@literal null}.
     * @param userId must not be {@literal null}
     * @return {@literal true} if a todoItem with the given id, belongs to the user with the given id {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null} or {@literal userId} is {@literal null}.
     */
    @Transactional(readOnly = true)
    boolean ownedByUser(Long id, Long userId);

    /**
     * Sets the todoItem status, done or not
     * @param done todoStatus
     */
    void setDone(Long id, boolean done);
}
