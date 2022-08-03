package com.github.youssefwadie.todo.dao.user;

import com.github.youssefwadie.todo.model.User;

import java.util.Optional;

public interface UserDao {

    Optional<User> findByEmail(String email);

    boolean existsById(Long id);

    boolean existsByEmail(String userEmail);

    User save(User user);
}
