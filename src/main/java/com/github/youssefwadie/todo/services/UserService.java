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
        Map<String, String> errors = new HashMap<>();
        if (!BasicValidator.isValidEmail(user.getEmail())) {
            errors.put("email", "Not a valid email");
        }
        if (BasicValidator.isBlank(user.getPassword())) {
            errors.put("password", "The password cannot be blank");
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
