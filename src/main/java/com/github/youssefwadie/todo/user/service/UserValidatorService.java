package com.github.youssefwadie.todo.user.service;

import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class UserValidatorService {
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,16}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    public static final String PASSWORD_VALIDATION_MESSAGE = """
            The password must contains at least 8 characters and at most 16 characters
            \t1-at least one digit.
            \t2-one lowercase alphabet.
            \t3-one uppercase alphabet.
            \t4-least one special character which includes !@#$%&*()-+=^.
            \t5-doesn't contain any white space.
            """;

    private final EmailValidator emailValidator = EmailValidator.getInstance(true);
    private final UserRepository userRepository;

    public boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public boolean isValidEmail(String email) {
        return emailValidator.isValid(email);
    }


    public void validateUser(User user) throws ConstraintsViolationException {
        final Map<String, String> errors = new HashMap<>();
        final String userEmail = user.getEmail();
        if (!isValidEmail(userEmail)) {
            errors.put("email", "Invalid email, please try different one");
        } else {
            if (user.getId() != null) {
                // NOT a new user
                if (!isUniqueEmail(user)) errors.put("email", "already taken");
            } else {
                if (userRepository.existsByEmail(userEmail)) errors.put("email", "already taken");
            }
        }

        // new user.
        if (user.getId() == null) {
            if (!isValidPassword(user.getPassword())) {
                errors.put("password", PASSWORD_VALIDATION_MESSAGE);
            }
        }

        if (!errors.isEmpty()) {
            throw new ConstraintsViolationException(errors);
        }
    }

    private boolean isUniqueEmail(User user) {
        String userEmail = user.getEmail();
        Optional<User> userOptionalFromTheDB = userRepository.findById(user.getId());
        // unlikely to happen
        if (userOptionalFromTheDB.isEmpty()) {
            throw new IllegalArgumentException("the passed user's id is not in the database");
        }

        User userFromTheDB = userOptionalFromTheDB.get();

        // password changed
        if (!userFromTheDB.getEmail().equals(user.getEmail())) {
            return !userRepository.existsByEmail(userEmail);
        }

        return true;
    }

}
