package com.github.youssefwadie.todo.user.controller;

import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/register")
public class RegistrationController {
    private final UserService service;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerNewUser(@RequestBody User user) {
        try {
            User savedUser = service.addUser(user);
            // TODO: send confirmation mail

            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ConstraintsViolationException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getErrors());
        }
    }
}
