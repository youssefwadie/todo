package com.github.youssefwadie.todo.security.exceptions;

import java.io.Serial;

public class InvalidAuthenticationSchemeException extends Exception {
    @Serial
    private static final long serialVersionUID = 5086046763662803426L;
    
    
    public InvalidAuthenticationSchemeException() {
        super();
    }

    public InvalidAuthenticationSchemeException(String message) {
        super(message);
    }
}
