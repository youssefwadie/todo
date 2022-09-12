package com.github.youssefwadie.todo.model;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String email;
    private String password;

}
