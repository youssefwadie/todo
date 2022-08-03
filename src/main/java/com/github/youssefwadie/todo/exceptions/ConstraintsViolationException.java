package com.github.youssefwadie.todo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
public class ConstraintsViolationException extends Exception {

    private final Map<String, String> errors;
    public ConstraintsViolationException(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
