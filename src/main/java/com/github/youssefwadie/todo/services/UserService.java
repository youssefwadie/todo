package com.github.youssefwadie.todo.services;

import com.github.youssefwadie.todo.dao.todo.TodoItemDao;
import com.github.youssefwadie.todo.dao.user.UserDao;
import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.exceptions.InvalidPasswordException;
import com.github.youssefwadie.todo.exceptions.UserNotFoundException;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.util.BasicValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {
    private final static int MIN_PASSWORD_LENGTH = 8;
    private final static int MAX_PASSWORD_LENGTH = 64;

    private final UserDao userDao;
    private final TodoItemDao todoDao;

    private final PasswordEncoder passwordEncoder;


    public User addUser(User user) throws ConstraintsViolationException {
        if (user.getId() != null) {
            user.setId(null);
        }
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

        if (!validatePassword(user.getPassword())) {
            errors.put("password", String.format("cannot be blank, min size = %d, max size = %d", MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
        }

        // new user
//        if (user.getId() == null) {
        // TODO: check password
//        }
        if (!errors.isEmpty()) {
            throw new ConstraintsViolationException(errors);
        }
    }

    private boolean validatePassword(String password) {
        return (!BasicValidator.isBlank(password) && BasicValidator.stringsSizeBetween(password, MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
    }

    public void changePassword(String oldPassword, String newPassword) throws UserNotFoundException, InvalidPasswordException {
        TodoUserDetails loggedInPrincipal = (TodoUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedInUser = loggedInPrincipal.getUser();
        User databaseUser = findById(loggedInUser.getId());
        if (!passwordEncoder.matches(oldPassword, databaseUser.getPassword())) {
            throw new InvalidPasswordException(InvalidPasswordException.PASSWORD_TYPE.OLD, "old password doesn't match the stored password");
        }
        boolean validPassword = validatePassword(newPassword);
        if (!validPassword) {
            throw new InvalidPasswordException(
                    InvalidPasswordException.PASSWORD_TYPE.NEW,
                    String.format("cannot be blank, min size = %d, max size = %d", MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        databaseUser.setPassword(encodedPassword);
        save(databaseUser);
    }

    public User save(User user) {
        return userDao.save(user);
    }

    public User findByEmail(String email) throws UserNotFoundException {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No User with email: %s was found!".formatted(email)));
    }


}
