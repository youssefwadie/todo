package com.github.youssefwadie.todo.user;

import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public boolean isValidEmail(String email) {
        return emailValidator.isValid(email);
    }


    public void validateUser(User user) throws ConstraintsViolationException {
        final Map<String, String> errors = new HashMap<>();
        final String userEmail = user.getEmail();
        if (!isValidEmail(userEmail)) {
            errors.put("email", "Not a valid email");
        } else {
            boolean emailAlreadyExists = userRepository.existsByEmail(userEmail);
            if (emailAlreadyExists) {
                errors.put("email", "the email %s is already owned by another account".formatted(userEmail));
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

}
