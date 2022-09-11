package com.github.youssefwadie.todo.security.util;


public final class BasicValidator {
    private BasicValidator() {
    }


    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }

}
