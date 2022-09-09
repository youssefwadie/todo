package com.github.youssefwadie.todo.controllers;


import com.github.youssefwadie.todo.constants.SecurityConstants;
import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.exceptions.InvalidPasswordException;
import com.github.youssefwadie.todo.exceptions.UserNotFoundException;
import com.github.youssefwadie.todo.model.ChangePasswordCommand;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.TokenProperties;
import com.github.youssefwadie.todo.security.util.BasicValidator;
import com.github.youssefwadie.todo.security.util.JwtUtils;
import com.github.youssefwadie.todo.services.UserService;
import com.github.youssefwadie.todo.util.SimpleResponseBody;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService service;
    private final TokenProperties tokenProperties;
    private final JwtUtils jwtUtils;

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<User> login() {
        TodoUserDetails loggedInPrincipal = (TodoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(loggedInPrincipal.getUser());
    }


    @GetMapping(value = "/refresh")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        Cookie refreshTokenCookie = getAccessTokenCookie(request.getCookies());

        if (refreshTokenCookie == null) {
            return unauthorizedResponse("Expected %s cookie".formatted(tokenProperties.getRefreshTokenCookieName()));
        }


        String jwt = refreshTokenCookie.getValue();
        try {
            LocalDateTime issueAt = jwtUtils.getIssueAt(jwt);
            User parsedUser = jwtUtils.parseUser(jwt, JwtUtils.TOKEN_TYPE.REFRESH);
            User userInDB = service.findById(parsedUser.getId());
            if (userInDB.getUpdatedAt() != null && userInDB.getUpdatedAt().isAfter(issueAt)) {

                Cookie cookie = new Cookie(refreshTokenCookie.getName(), null);
                cookie.setPath("/api/v1/users");
                cookie.setMaxAge(0);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                response.addCookie(cookie);

                SecurityContextHolder.getContext().setAuthentication(null);
                return unauthorizedResponse("user details has been changed, please re-login.");
            }

            Date now = new Date();
            String accessToken = Jwts.builder()
                    .setIssuer("Todo")
                    .setSubject(userInDB.getEmail())
                    .claim(SecurityConstants.USER_ID_CLAIM_NAME, userInDB.getId())
                    .claim(SecurityConstants.TOKEN_TYPE_CLAIM_NAME, SecurityConstants.TOKEN_TYPE_ACCESS_CLAIM_VALUE)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + tokenProperties.getAccessTokenLifeTime()))
                    .signWith(tokenProperties.getSecretKey())
                    .compact();

            SimpleResponseBody token = new SimpleResponseBody
                    .Builder(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase())
                    .setMessage("access token is refreshed, can be found in header: %s`".formatted(tokenProperties.getAccessTokenHeaderNameGeneratedByServer()))
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(tokenProperties.getAccessTokenHeaderNameGeneratedByServer(), accessToken)
                    .body(token);
        } catch (Exception ex) {
            SimpleResponseBody error = new SimpleResponseBody
                    .Builder(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .setMessage(ex.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PutMapping(value = "/change-password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordCommand changePasswordCommand) throws UserNotFoundException {

        Map<String, String> errors = new HashMap<>();
        String oldPassword = changePasswordCommand.getOldPassword();
        String newPassword = changePasswordCommand.getNewPassword();

        if (BasicValidator.isBlank(oldPassword)) errors.put("oldPassword", "cannot be empty");
        if (BasicValidator.isBlank(newPassword)) errors.put("newPassword", "cannot be empty");

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            service.changePassword(oldPassword, newPassword);
        } catch (InvalidPasswordException ex) {
            InvalidPasswordException.PASSWORD_TYPE passwordType = ex.getPasswordType();
            if (passwordType.equals(InvalidPasswordException.PASSWORD_TYPE.OLD)) {
                errors.put("oldPassword", ex.getMessage());
            } else if (passwordType.equals(InvalidPasswordException.PASSWORD_TYPE.NEW)) {
                errors.put("newPassword", ex.getMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User savedUser = service.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ConstraintsViolationException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getErrors());
        }
    }

    private ResponseEntity<Object> unauthorizedResponse(String message) {
        SimpleResponseBody simpleResponseBody = new SimpleResponseBody
                .Builder(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .setMessage(message)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(simpleResponseBody);
    }

    private Cookie getAccessTokenCookie(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(tokenProperties.getRefreshTokenCookieName())) return cookie;
        }
        return null;
    }
}
