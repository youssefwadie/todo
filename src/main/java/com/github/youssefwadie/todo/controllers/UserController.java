package com.github.youssefwadie.todo.controllers;


import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.youssefwadie.todo.constants.SecurityConstants;
import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.util.JwtUtils;
import com.github.youssefwadie.todo.security.util.JwtUtils.TOKEN_TYPE;
import com.github.youssefwadie.todo.security.util.SimpleResponseBody;
import com.github.youssefwadie.todo.services.UserService;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService service;


    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<User> login() {
        TodoUserDetails loggedInPrincipal = (TodoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(loggedInPrincipal.getUser());
    }


    @GetMapping(value = "/refresh-token", produces = "application/json")
    public ResponseEntity<Object> refreshToken(@RequestHeader(value = SecurityConstants.JWT_HEADER, required = false) String authorizationHeader) {
        if (authorizationHeader == null) {
            SimpleResponseBody simpleResponseBody = new SimpleResponseBody
                    .Builder(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .setMessage("Expected %s header".formatted(SecurityConstants.JWT_HEADER))
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(simpleResponseBody);
        }

        if (!authorizationHeader.startsWith(SecurityConstants.JWT_AUTHENTICATION_SCHEME)) {
            SimpleResponseBody simpleResponseBody = new SimpleResponseBody
                    .Builder(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .setMessage("Expected %s header starts with: %s".formatted(SecurityConstants.JWT_HEADER, SecurityConstants.JWT_AUTHENTICATION_SCHEME))
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(simpleResponseBody);
        }

        try {
            authorizationHeader = JwtUtils.extractAccessToken(authorizationHeader);
            User user = JwtUtils.parseUser(authorizationHeader, TOKEN_TYPE.REFRESH);
            Date now = new Date();
            String accessToken = Jwts.builder()
                    .setIssuer("Todo")
                    .setSubject(user.getEmail())
                    .claim(SecurityConstants.USER_ID_CLAIM_NAME, user.getId())
                    .claim(SecurityConstants.TOKEN_TYPE_CLAIM_NAME, SecurityConstants.TOKEN_TYPE_ACCESS_CLAIM_VALUE)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + SecurityConstants.ACCESS_TOKEN_LIFE_TIME))
                    .signWith(SecurityConstants.SECRET_KEY)
                    .compact();

            SimpleResponseBody token = new SimpleResponseBody
                    .Builder(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase())
                    .setMessage("access token is refreshed, found in header: " + SecurityConstants.JWT_HEADER)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).header(SecurityConstants.JWT_HEADER,
                    SecurityConstants.JWT_AUTHENTICATION_SCHEME + " " + accessToken).body(token);

        } catch (Exception ex) {
            SimpleResponseBody error = new SimpleResponseBody
                    .Builder(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .setMessage(ex.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }


    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User savedUser = service.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ConstraintsViolationException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getErrors());
        }
    }
}
