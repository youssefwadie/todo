package com.github.youssefwadie.todo.services;

import com.github.youssefwadie.todo.dao.todo.TodoItemDao;
import com.github.youssefwadie.todo.dao.user.UserDao;
import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.exceptions.UserNotFoundException;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.util.BasicValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final TodoItemDao todoDao;

    private final PasswordEncoder passwordEncoder;


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

        if (BasicValidator.isBlank(user.getPassword())) {
            errors.put("password", "cannot be blank");
        }

        // new user
//        if (user.getId() == null) {
        // TODO: check password
//        }
        if (!errors.isEmpty()) {
            throw new ConstraintsViolationException(errors);
        }
    }

    public User findByEmail(String email) throws UserNotFoundException {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No User with email: %s was found!".formatted(email)));
    }


}
