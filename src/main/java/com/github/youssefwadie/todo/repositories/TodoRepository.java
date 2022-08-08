package com.github.youssefwadie.todo.repositories;

import com.github.youssefwadie.todo.model.Todo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TodoRepository extends CrudRepository<Todo, Long> {
    /**
     * finds all the todos for given userId
     * @param userId must not be {@literal null}.
     * @return all the todos for given user by their id
     * @throws IllegalArgumentException in case the given id is null
     */
    @Transactional(readOnly = true)
    @Query("FROM Todo WHERE userId = ?1")
    Iterable<Todo> findAllByUserId(Long userId);

    /**
     * deletes all the todos' for given userId
     * @param userId must not be {@literal null}
     * @throws IllegalArgumentException if the userId is {@literal null}
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Todo WHERE userId = ?1")
    void deleteAllByUserId(Long userId);

    /**
     * count all the todos created by a user
     * @param userId must not be {@literal null}
     * @return the number of todos for a given user
     * @throws IllegalArgumentException if userId is {@literal null}
     */
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userId = ?1")
    long countByUserId(Long userId);

    /**
     * Returns whether todo with the given id is owned by a user with the given userId.
     *
     * @param id must not be {@literal null}.
     * @param userId must not be {@literal null}
     * @return {@literal true} if a todo with the given id, belongs to the user with the given id {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null} or {@literal userId} is {@literal null}.
     */
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(t) > 0 FROM Todo t WHERE t.id = ?1 AND t.userId = ?2")
    boolean belongsToUser(Long id, Long userId);
}
