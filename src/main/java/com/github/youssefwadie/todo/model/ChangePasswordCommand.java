package com.github.youssefwadie.todo.model;

import lombok.Data;

@Data
public class ChangePasswordCommand {
    private String oldPassword;
    private String newPassword;
}
