package com.github.youssefwadie.todo.util;

import java.time.LocalDateTime;

import org.apache.commons.validator.routines.EmailValidator;

public class BasicValidator {
    private static final EmailValidator emailValidator = EmailValidator.getInstance(true);

    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }

    public static boolean isInThePast(LocalDateTime dateTime) {
        return dateTime == null || dateTime.isBefore(LocalDateTime.now());
    }

    public static boolean isValidEmail(String email) {
        return emailValidator.isValid(email);
    }

}
