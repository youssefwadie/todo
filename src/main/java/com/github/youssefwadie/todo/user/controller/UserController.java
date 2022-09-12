package com.github.youssefwadie.todo.user.controller;


import com.github.youssefwadie.todo.model.ChangePasswordRequest;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.JwtService;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.TokenProperties;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.security.exceptions.InvalidPasswordException;
import com.github.youssefwadie.todo.security.exceptions.UserNotFoundException;
import com.github.youssefwadie.todo.security.util.BasicValidator;
import com.github.youssefwadie.todo.user.service.UserService;
import com.github.youssefwadie.todo.util.SimpleResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;
    private final TokenProperties tokenProperties;
    private final JwtService jwtService;

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
            LocalDateTime issueAt = jwtService.parseIssuedAt(jwt);
            User parsedUser = jwtService.parseUser(jwt, JwtService.TOKEN_TYPE.REFRESH);
            User userInDB = service.findById(parsedUser.getId());

            if (!userInDB.isEnabled()) return unauthorizedResponse("the account is not disabled");

            if (userInDB.getUpdatedAt() != null && userInDB.getUpdatedAt().isAfter(issueAt)) {
                removeRefreshTokenCookie(response);
                return unauthorizedResponse("please re-login.");
            }

            List<String> simpleAuthorities = jwtService.getSimpleUserAuthorities(userInDB.getRoles());
            String accessToken = jwtService.generatedAccessToken(userInDB.getId(), userInDB.getEmail(), simpleAuthorities);

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

    @PostMapping(value = "logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        removeRefreshTokenCookie(response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PutMapping(value = "/change-password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) throws UserNotFoundException {

        Map<String, String> errors = new HashMap<>();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();

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
        } catch (ConstraintsViolationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        }

        return ResponseEntity.ok().build();
    }


    private void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(tokenProperties.getRefreshTokenCookieName(), null);
        cookie.setPath("/api/v1/users");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        SecurityContextHolder.getContext().setAuthentication(null);

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
