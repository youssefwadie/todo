package com.github.youssefwadie.todo.user.controller;

import com.github.youssefwadie.todo.model.RegistrationRequest;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.user.service.RegistrationService;
import com.github.youssefwadie.todo.util.SimpleResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerNewUser(@RequestBody RegistrationRequest registrationRequest) {
        try {
            String message = registrationService.register(registrationRequest);

            final SimpleResponseBody responseBody =
                    new SimpleResponseBody
                            .Builder(HttpStatus.OK)
                            .setMessage("checkout your mail box").build();

            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (ConstraintsViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getErrors());
        }
    }

    @GetMapping("confirm")
    public ResponseEntity<?> confirmToken(@RequestParam("token") String token) {
        try {
            String message = registrationService.confirmToken(token);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException ex) {
            SimpleResponseBody responseBody =
                    new SimpleResponseBody.Builder(HttpStatus.BAD_REQUEST).setMessage(ex.getMessage()).build();
            return ResponseEntity.badRequest().body(responseBody);
        }
    }
}
