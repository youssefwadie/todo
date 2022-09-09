package com.github.youssefwadie.todo.exceptions;


import java.io.Serial;

public class InvalidPasswordException extends Exception {

    @Serial
    private static final long serialVersionUID = 5086046763662803426L;

    public enum PASSWORD_TYPE {
        OLD,
        NEW,
    }

    private final PASSWORD_TYPE passwordType;

    public InvalidPasswordException(PASSWORD_TYPE passwordType) {
        super();
        this.passwordType = passwordType;
    }

    public InvalidPasswordException(PASSWORD_TYPE passwordType, String message) {
        super(message);
        this.passwordType = passwordType;
    }

    public PASSWORD_TYPE getPasswordType() {
        return passwordType;
    }
}
