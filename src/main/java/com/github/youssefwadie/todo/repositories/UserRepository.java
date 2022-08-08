package com.github.youssefwadie.todo.repositories;

import com.github.youssefwadie.todo.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * Retrieves a user by their email.
     *
     * @param email must not be {@literal null}.
     * @return the user with the given email or {@link Optional#empty()} if non found.
     * @throws IllegalArgumentException in case the given {@literal user} is {@literal null}.
     */
    @Transactional(readOnly = true)
    @Query("FROM User WHERE email = ?1")
    Optional<User> findByEmail(String email);


    /**
     * Returns whether user with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return {@literal true} if a user with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = ?1")
    boolean existsById(Long id);


    /**
     * Returns whether user with the given email exists.
     *
     * @param email must not be {@literal null}.
     * @return {@literal true} if a user with the given email exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = ?1")
    boolean existsByEmail(String email);

    /**
     * Deletes the user with the given email.
     *
     * @param email must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal email} is {@literal null}
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.email = ?1")
    void deleteByEmail(String email);

}
