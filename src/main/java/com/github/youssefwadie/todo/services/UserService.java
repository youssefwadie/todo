package com.github.youssefwadie.todo.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.youssefwadie.todo.dao.todo.TodoDao;
import com.github.youssefwadie.todo.dao.user.UserDao;
import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.exceptions.UserNotFoundException;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.util.BasicValidator;

@Service
public class UserService {
    private final UserDao userDao;
    private final TodoDao todoDao;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, TodoDao todoDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.todoDao = todoDao;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(User user) throws ConstraintsViolationException {
        validateUser(user);
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);
        return userDao.save(user);
    }

    public boolean existsById(Long userId) {
        return userDao.existsById(userId);
    }

    public void deleteUser(User user) {
        todoDao.deleteAllByUserId(user.getId());
        userDao.delete(user);
    }

    public User findById(Long userId) throws UserNotFoundException {
        return userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No User with id: %d was found!".formatted(userId)));
    }


    public void validateUser(User user) throws ConstraintsViolationException {
        final Map<String, String> errors = new HashMap<>();
        final String userEmail = user.getEmail();
        if (!BasicValidator.isValidEmail(userEmail)) {
            errors.put("email", "Not a valid email");
        } else {
            boolean emailAlreadyExists = userDao.existsByEmail(userEmail);
            if (emailAlreadyExists) {
                errors.put("email", "the email %s is already owned by another account".formatted(userEmail));
            }
        }

        // new user
//        if (user.getId() == null) {
        // TODO: check password
//        }
        if (!errors.isEmpty()) {
            throw new ConstraintsViolationException(errors);
        }
    }


}
