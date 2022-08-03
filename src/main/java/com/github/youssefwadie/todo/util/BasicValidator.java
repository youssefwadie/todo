package com.github.youssefwadie.todo.util;

import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDateTime;

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