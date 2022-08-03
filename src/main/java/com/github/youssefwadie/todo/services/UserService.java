package com.github.youssefwadie.todo.services;

import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.repositories.UserRepository;
import com.github.youssefwadie.todo.util.BasicValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(User user) throws ConstraintsViolationException {
        validateUser(user);
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }


    public void validateUser(User user) throws ConstraintsViolationException {
        final Map<String, String> errors = new HashMap<>();
        final String userEmail = user.getEmail();
        if (!BasicValidator.isValidEmail(userEmail)) {
            errors.put("email", "Not a valid email");
        } else {
            boolean emailAlreadyExists = userRepository.existsByEmail(userEmail);
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
