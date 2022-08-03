package com.github.youssefwadie.todo.exceptions;

import java.util.Map;

public class ConstraintsViolationException extends Exception {

    private final Map<String, String> errors;
    public ConstraintsViolationException(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
