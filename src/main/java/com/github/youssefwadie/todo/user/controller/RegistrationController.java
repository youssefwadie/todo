package com.github.youssefwadie.todo.user.controller;

import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.model.UserRegistrationRequest;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.user.service.RegistrationService;
import com.github.youssefwadie.todo.user.service.UserService;
import com.github.youssefwadie.todo.user.confirmationtoken.ConfirmationTokenService;
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
    private final ConfirmationTokenService confirmationTokenService;
    private final RegistrationService registrationService;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerNewUser(@RequestBody UserRegistrationRequest registrationRequest) {
        try {
            User savedUser = registrationService.addUser(registrationRequest);
            confirmationTokenService.addConfirmationTokenForUser(savedUser.getId());
            // TODO: send confirmation mail
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ConstraintsViolationException ex) {
            return ResponseEntity.badRequest().body(ex.getErrors());
        }
    }
}
