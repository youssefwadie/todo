package com.github.youssefwadie.todo.security.util;

import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDateTime;

public final class BasicValidator {
    private BasicValidator() {
    }

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

    public static boolean stringsSizeNotBetween(String str, int min, int max) {
        if (str == null) return true;
        int length = str.length();
        return (length < min || length > max);
    }
    public static boolean stringsSizeBetween(String str, int min, int max) {
        return !stringsSizeNotBetween(str, min, max);
    }
}
