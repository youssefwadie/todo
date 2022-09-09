package com.github.youssefwadie.todo.exceptions;


public class InvalidPasswordException extends Exception {
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
