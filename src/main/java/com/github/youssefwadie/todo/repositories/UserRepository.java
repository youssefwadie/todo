package com.github.youssefwadie.todo.repositories;

import com.github.youssefwadie.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = ?1")
    boolean existsById(Long id);
}
