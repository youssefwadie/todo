package com.github.youssefwadie.todo.email;


public interface EmailSender {
    void send(String subject, String email, String to);
}
