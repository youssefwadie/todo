package com.github.youssefwadie.todo.exceptions;

import java.io.Serial;

public class UserNotFoundException extends Exception {

	@Serial
    private static final long serialVersionUID = 5086046763662803426L;
	
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
