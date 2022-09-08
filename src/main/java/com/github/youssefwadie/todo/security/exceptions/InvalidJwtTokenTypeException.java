package com.github.youssefwadie.todo.security.exceptions;

import java.io.Serial;

public class InvalidJwtTokenTypeException extends Exception {
    @Serial
    private static final long serialVersionUID = 5086046763662803426L;

    public InvalidJwtTokenTypeException() {
        super();
    }

    public InvalidJwtTokenTypeException(String message) {
        super(message);
    }
}
